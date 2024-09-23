package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingComment
import com.waddle.gentoo.internal.api.response.FloatingProduct
import com.waddle.gentoo.internal.exception.GentooException
import com.waddle.gentoo.internal.util.urlEncoded
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO(nathan) : Refactor main class after designing public interface. It's just placeholder at this moment.
object Gentoo {
    private val apiClient: ApiClient = ApiClient(
        BuildConfig.API_KEY,
        BuildConfig.DAILY_SHOT_BASE_URL
    )

    @Throws(GentooException::class)
    // TODO(nathan) : check if it is okay to provide suspend function only
    suspend fun getDetailChatUrl(
        userDeviceId: String,
        authCode: String,
        clientId: String,
        itemId: String,
        type: ChatType
    ): String {
        val authResponse = authenticate(userDeviceId, authCode)
        val userId = authResponse.body.randomId
        val floatingComment = with(fetchFloatingComment(itemId, userId)) {
            when (type) {
                ChatType.THIS -> this.commentForThis
                ChatType.NEEDS -> this.commentForNeeds
            }
        }
        val hostUrl = if (clientId == "dlst") {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/${clientId.urlEncoded}/sdk/${userId.urlEncoded}?i=${itemId.urlEncoded}&u=${userId.urlEncoded}&t=${type.asString.urlEncoded}&ch=true&fc=${floatingComment.urlEncoded}" // this.chatUrl = `${hostSrc}/dlst/sdk/${userId}?i=${itemId}&u=${userId}&t=${type}&ch=true&fc=${floatingComment}`
    }

    suspend fun getHomeChatUrl(
        userDeviceId: String,
        authCode: String,
        clientId: String,
    ): String {
        val authResponse = authenticate(userDeviceId, authCode)
        val userId = authResponse.body.randomId
        val hostUrl = if (clientId == "dlst") {
            "https://demo.gentooai.com"
        } else {
            "https://dev-demo.gentooai.com"
        }
        return "$hostUrl/${clientId.urlEncoded}/${userId.urlEncoded}?ch=true" // `${hostSrc}/dlst/${userId}?ch=true`
    }

    @Throws(GentooException::class)
    suspend fun fetchFloatingComment(itemId: String, userId: String): FloatingComment {
        val floatingCommentRequest = FloatingCommentRequest(itemId, userId)
        return when (val floatingComment = apiClient.send(floatingCommentRequest, FloatingComment.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingComment.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingComment.value
        }
    }

    @Throws(GentooException::class)
    suspend fun fetchFloatingProduct(itemId: String, userId: String, target: String): FloatingProduct {
        val floatingProductRequest = FloatingProductRequest(itemId, userId, target)
        return when (val floatingProduct = apiClient.send(floatingProductRequest, FloatingProduct.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingProduct.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingProduct.value
        }
    }

    @Throws(GentooException::class)
    private suspend fun authenticate(userDeviceId: String, authCode: String): AuthResponse {
        val authRequest = AuthRequest(userDeviceId, authCode)
        return when (val authResponse = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Failure -> throw GentooException(authResponse.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> authResponse.value
        }
    }
}
