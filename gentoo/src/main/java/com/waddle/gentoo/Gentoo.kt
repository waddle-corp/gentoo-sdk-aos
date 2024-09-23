package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.response.AuthInfo
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingComment
import com.waddle.gentoo.internal.api.response.FloatingProduct
import com.waddle.gentoo.internal.exception.GentooException
import com.waddle.gentoo.internal.util.urlEncoded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

// TODO(nathan) : Refactor main class after designing public interface. It's just placeholder at this moment.
object Gentoo {
    private val apiClient: ApiClient = ApiClient(
        BuildConfig.API_KEY,
        BuildConfig.DAILY_SHOT_BASE_URL
    )

    private var initializeParams: InitializeParams? = null
    private var authInfo: AuthInfo? = null

    /**
     * If it is not null, it means SDK has not been initialized yet.
     */
    private var authJob: Deferred<AuthResponse>? = null

    @Synchronized
    fun initialize(params: InitializeParams) {
        // If SDK already has been initialized with same initializeParams, do nothing.
        if (params == this.initializeParams) return
        if (this.initializeParams != null) {
            // If initialize is requested while SDK has been initialized with different InitializedParams.
            // reset existing resources
            authJob?.cancel()
        }

        this.initializeParams = params
        authJob = CoroutineScope(Dispatchers.IO).async {
            authenticate(params.udid, params.authCode)
            // TODO catch authenticate's exception here.
        }
    }

    @Throws(GentooException::class)
    // TODO(nathan) : check if it is okay to provide suspend function only
    suspend fun getDetailChatUrl(
        itemId: String,
        type: ChatType,
        comment: String
    ): String {
        val initializeParams = this.initializeParams ?: throw GentooException("Initialize should be called first")
        val authResponse = authJob?.await() ?: throw GentooException("Initialize should be called first")
        val userId = authResponse.randomId
        val hostUrl = if (initializeParams.clientId == "dlst") {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/${initializeParams.clientId.urlEncoded}/sdk/${userId.urlEncoded}?i=${itemId.urlEncoded}&u=${userId.urlEncoded}&t=${type.asString.urlEncoded}&ch=true&fc=${comment.urlEncoded}" // this.chatUrl = `${hostSrc}/dlst/sdk/${userId}?i=${itemId}&u=${userId}&t=${type}&ch=true&fc=${floatingComment}`
    }

    suspend fun getHomeChatUrl(): String {
        val initializeParams = this.initializeParams ?: throw GentooException("Initialize should be called first")
        val authResponse = authJob?.await() ?: throw GentooException("Initialize should be called first")
        val userId = authResponse.randomId
        val hostUrl = if (initializeParams.clientId == "dlst") {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/${initializeParams.clientId.urlEncoded}/${userId.urlEncoded}?ch=true" // `${hostSrc}/dlst/${userId}?ch=true`
    }

    @Throws(GentooException::class)
    suspend fun fetchFloatingComment(itemId: String): FloatingComment {
        val authResponse = authJob?.await() ?: throw GentooException("Initialize should be called first")
        val floatingCommentRequest = FloatingCommentRequest(itemId, authResponse.randomId)
        return when (val floatingComment = apiClient.send(floatingCommentRequest, FloatingComment.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingComment.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingComment.value
        }
    }

    @Throws(GentooException::class)
    suspend fun fetchFloatingProduct(itemId: String, target: String): FloatingProduct {
        val authResponse = authJob?.await() ?: throw GentooException("Initialize should be called first")
        val floatingProductRequest = FloatingProductRequest(itemId, authResponse.randomId, target)
        return when (val floatingProduct = apiClient.send(floatingProductRequest, FloatingProduct.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingProduct.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingProduct.value
        }
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
