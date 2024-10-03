package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.request.UserEventCategory
import com.waddle.gentoo.internal.api.request.UserEventRequest
import com.waddle.gentoo.internal.api.response.AuthInfo
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingComment
import com.waddle.gentoo.internal.api.response.FloatingProduct
import com.waddle.gentoo.internal.api.response.UserEventResponse
import com.waddle.gentoo.internal.exception.GentooException
import com.waddle.gentoo.internal.util.urlEncoded
import com.waddle.gentoo.ui.GentooFloatingActionButton
import com.waddle.gentoo.viewmodel.GentooViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Gentoo {
    private val apiClient: ApiClient = ApiClient(
        BuildConfig.API_KEY,
        BuildConfig.DAILY_SHOT_BASE_URL
    )

    private var _initializeParams: InitializeParams? = null
    private val initializeParams: InitializeParams
        get() = _initializeParams ?: throw GentooException("Initialize should be called first")

    private var authInfo: AuthInfo? = null

    /**
     * If it is not null, it means SDK has not been initialized yet.
     */
    private var authJob: Deferred<AuthResponse>? = null

    @Synchronized
    fun initialize(params: InitializeParams) {
        // If SDK already has been initialized with same initializeParams, do nothing.
        if (params == this._initializeParams) return
        if (this._initializeParams != null) {
            // If initialize is requested while SDK has been initialized with different InitializedParams.
            // reset existing resources
            authJob?.cancel()
        }

        this._initializeParams = params
        authJob = CoroutineScope(Dispatchers.IO).async {
            authenticate(params.udid, params.authCode)
            // TODO catch authenticate's exception here.
        }
    }

    fun bind(
        view: GentooFloatingActionButton,
        viewModel: GentooViewModel,
        lifecycleScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    ) {
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
        view.onClick = { viewModel.onClicked() }
    }

    @Throws(GentooException::class)
    internal suspend fun getDetailChatUrl(
        itemId: String,
        type: ChatType,
        comment: String
    ): String {
        val (initializeParams, authResponse) = awaitAuth()
        val userId = authResponse.randomId
        val hostUrl = if (initializeParams.clientId == "dlst" && BuildConfig.DEBUG.not()) {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/${initializeParams.clientId.urlEncoded}/sdk/${userId.urlEncoded}?i=${itemId.urlEncoded}&t=${type.asString.urlEncoded}&ch=true&fc=${comment.urlEncoded}" // this.chatUrl = `${hostSrc}/dlst/sdk/${userId}?i=${itemId}&u=${userId}&t=${type}&ch=true&fc=${floatingComment}`
    }

    internal suspend fun getDefaultChatUrl(): String {
        val (initializeParams, authResponse) = awaitAuth()
        val userId = authResponse.randomId
        val hostUrl = if (initializeParams.clientId == "dlst" && BuildConfig.DEBUG.not()) {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/${initializeParams.clientId.urlEncoded}/${userId.urlEncoded}?ch=true" // `${hostSrc}/dlst/${userId}?ch=true`
    }

    @Throws(GentooException::class)
    internal suspend fun fetchFloatingComment(chatType: ChatType, itemId: String): FloatingComment { // TODO : cache
        val (params, authResponse) = awaitAuth()
        val floatingCommentRequest = FloatingCommentRequest(params.clientId, itemId, authResponse.randomId, chatType)
        return when (val floatingComment = apiClient.send(floatingCommentRequest, FloatingComment.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingComment.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingComment.value
        }
    }

    @Throws(GentooException::class)
    internal suspend fun fetchFloatingProduct(itemId: String, target: String): FloatingProduct {
        val (_, authResponse) = awaitAuth()
        val floatingProductRequest = FloatingProductRequest(itemId, authResponse.randomId, target)
        return when (val floatingProduct = apiClient.send(floatingProductRequest, FloatingProduct.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingProduct.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingProduct.value
        }
    }

    internal suspend fun sendUserEvent(
        userEventCategory: UserEventCategory,
        itemId: String? = null
    ) {
        val (_, authResponse) = awaitAuth()
        val userEventRequest = UserEventRequest(
            userEventCategory = userEventCategory,
            userId = authResponse.randomId,
            clientId = initializeParams.clientId,
            itemId
        )
        return when (val response = apiClient.send(userEventRequest, UserEventResponse.serializer())) {
            is GentooResponse.Failure -> {
            }
            is GentooResponse.Success -> {

            }
        }
    }

    private suspend fun awaitAuth(): Pair<InitializeParams, AuthResponse> {
        val initializeParams = this.initializeParams
        val authResponse = authJob?.await() ?: throw GentooException("Initialize should be called first")
        return initializeParams to authResponse
    }

    @Throws(GentooException::class)
    private suspend fun authenticate(udid: String, authCode: String): AuthResponse {
        // if there is same auth info with given userDeviceId and authCode, early return cached AuthResponse
        this.authInfo?.let {
            if (it.udid == udid && it.authCode == authCode) return it.authResponse
        }

        val authRequest = AuthRequest(udid, authCode)
        return when (val authResponse = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Failure -> throw GentooException(authResponse.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> {
                this.authInfo = AuthInfo(udid, authCode, authResponse.value)
                authResponse.value
            }
        }
    }
}
