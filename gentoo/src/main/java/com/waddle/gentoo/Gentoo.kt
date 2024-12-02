package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingData
import com.waddle.gentoo.internal.api.request.UserEventCategory
import com.waddle.gentoo.internal.api.request.UserEventRequest
import com.waddle.gentoo.internal.api.response.AuthInfo
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingComment
import com.waddle.gentoo.internal.api.response.UserEventResponse
import com.waddle.gentoo.internal.exception.GentooException
import com.waddle.gentoo.internal.util.Constants
import com.waddle.gentoo.internal.util.urlEncoded
import com.waddle.gentoo.ui.GentooFloatingActionButton
import com.waddle.gentoo.viewmodel.GentooViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor

object Gentoo {
    private val apiClient: ApiClient = ApiClient(
        Constants.API_KEY,
        Constants.BASE_URL
    )

    private var _initializeParams: InitializeParams? = null
    private val initializeParams: InitializeParams
        get() = _initializeParams ?: throw GentooException("Initialize should be called first")

    private var authInfo: AuthInfo? = null

    /**
     * If it is not null, it means SDK has not been initialized yet.
     */
    private var authJob: Deferred<AuthResponse>? = null

    var logLevel: LogLevel
        get() = Logger.loggerLevel
        set(value) {
            if (value == LogLevel.DEBUG) {
                apiClient.httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                apiClient.httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            Logger.loggerLevel = value
        }

    @Synchronized
    fun initialize(params: InitializeParams) {
        Logger.d("Gentoo.initialize(params: $params)")
        // If SDK already has been initialized with same initializeParams, do nothing.
        if (params == this._initializeParams) {
            Logger.d("Gentoo.initialize() >> already initialized")
            return
        }
        if (this._initializeParams != null) {
            // If initialize is requested while SDK has been initialized with different InitializedParams.
            // reset existing resources
            Logger.d("Gentoo.initialize() >> already initialized with different params.")
            authJob?.cancel()
        }

        this._initializeParams = params
        authJob = CoroutineScope(Dispatchers.IO).async {
            try {
                authenticate(params.udid, params.userToken)
            } catch (e: Exception) {
                Logger.e("Gentoo.initialize() >> failed to authenticate(e: $e) ")
                throw e
            }
        }
    }

    fun bind(
        view: GentooFloatingActionButton,
        viewModel: GentooViewModel,
        lifecycleScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    ) {
        Logger.d("Gentoo.bind(view: $view, viewModel: $viewModel, lifecycleScope: $lifecycleScope)")
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                view.uiState = state
            }
        }

        lifecycleScope.launch {
            viewModel.chatUrl.collect { url ->
                view.chatUrl = url
            }
        }

        view.onDismiss = { viewModel.onBottomSheetDismissed() }
        view.onClick = {
            viewModel.onClicked()
            viewModel.markAsFloatingButtonClicked()
        }
        view.onViewRendered = { viewModel.markAsFloatingButtonRendered() }
        view.onGifAnimationEnded = { viewModel.onGifAnimationEnded() }
        view.onTextAnimationEnded = { viewModel.onTextAnimationEnded() }
        viewModel.showFloatingButtonComment()
    }

    @Throws(GentooException::class)
    internal suspend fun getDetailChatUrl(
        itemId: String,
        type: CommentType,
        comment: String
    ): String {
        Logger.d("Gentoo.getDetailChatUrl(itemId: $itemId, type: $type, comment: $comment)")
        val (initializeParams, authResponse) = awaitAuth()
        val userId = authResponse.chatUserId
        val hostUrl = if (BuildConfig.DEBUG.not()) {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/chatroute/gentoo?ptid=${initializeParams.partnerId.urlEncoded}&cuid=${userId.urlEncoded}&i=${itemId.urlEncoded}&t=${type.asString.urlEncoded}&ch=true&fc=${comment.urlEncoded}".also {
            Logger.d("Gentoo.getDetailChatUrl() >> built chat url: $it")
        }
    }

    internal suspend fun getDefaultChatUrl(): String {
        Logger.d("Gentoo.getDefaultChatUrl()")
        val (initializeParams, authResponse) = awaitAuth()
        val hostUrl = if (BuildConfig.DEBUG.not()) {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/chatroute/gentoo?ptid=${initializeParams.partnerId.urlEncoded}&cuid=${authResponse.chatUserId.urlEncoded}&ch=true".also {
            Logger.d("Gentoo.getDefaultChatUrl() >> built chat url: $it")
        }
    }

    @Throws(GentooException::class)
    internal suspend fun fetchFloatingComment(floatingData: FloatingData): FloatingComment {
        Logger.d("Gentoo.fetchFloatingComment(floatingData: $floatingData)")
        val params = this.initializeParams
        val floatingCommentRequest = FloatingCommentRequest(params.partnerId, floatingData)
        return when (val floatingComment = apiClient.send(floatingCommentRequest, FloatingComment.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingComment.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingComment.value
        }
    }

    internal suspend fun sendUserEvent(
        userEventCategory: UserEventCategory,
        itemId: String? = null
    ) {
        val (_, authResponse) = awaitAuth()
        val userEventRequest = UserEventRequest(
            userEventCategory = userEventCategory,
            userId = authResponse.chatUserId,
            clientId = initializeParams.partnerId,
            itemId
        )

        return try {
            when (val response = apiClient.send(userEventRequest, UserEventResponse.serializer())) {
                is GentooResponse.Failure -> {
                    Logger.d("Failed to log user event. ${response.errorResponse}")
                }
                is GentooResponse.Success -> {
                    Logger.d("Succeeded to log user event. ${response.value.message}")
                }
            }
        } catch (e: GentooException) {
            Logger.e("Failed to log user event by error. e: $e")
        }
    }

    internal suspend fun awaitAuth(): Pair<InitializeParams, AuthResponse> {
        val initializeParams = this.initializeParams
        val authResponse = authJob?.await() ?: throw GentooException("Initialize should be called first")
        return initializeParams to authResponse
    }

    @Throws(GentooException::class)
    private suspend fun authenticate(udid: String, userToken: String): AuthResponse {
        Logger.d("Gentoo.authenticate(udid: $udid)")
        // if there is same auth info with given userDeviceId and authCode, early return cached AuthResponse
        this.authInfo?.let {
            Logger.d("Gentoo.authenticate() >> already authenticated with udid($udid)")
            if (it.udid == udid && it.userToken == userToken) return it.authResponse
        }

        val authRequest = AuthRequest(udid, userToken)
        return when (val authResponse = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Failure -> throw GentooException(authResponse.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> {
                this.authInfo = AuthInfo(udid, userToken, authResponse.value)
                authResponse.value
            }
        }
    }
}
