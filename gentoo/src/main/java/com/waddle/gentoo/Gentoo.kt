package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingCommentResponse
import com.waddle.gentoo.internal.api.response.FloatingProductResponse
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
    suspend fun getChatUrl(
        userDeviceId: String,
        authCode: String,
        clientId: String,
        itemId: String
    ): String {
        val authResponse = authenticate(userDeviceId, authCode)
        val userId = authResponse.body.randomId
        val floatingProductResponse = getFloatingProduct(itemId, userId, "this")
        val floatingProductAsJson = Json.encodeToString(floatingProductResponse)
        return "${BuildConfig.DAILY_SHOT_BASE_URL}/${clientId.urlEncoded}/sdk/${userId.urlEncoded}?product=${floatingProductAsJson.urlEncoded}" // ${this.hostSrc}/${this.clientId}/sdk/${this.userId}?product=${JSON.stringify(this.floatingProduct)}
    }

    @Throws(GentooException::class)
    private suspend fun authenticate(userDeviceId: String, authCode: String): AuthResponse {
        val authRequest = AuthRequest(userDeviceId, authCode)
        return when (val authResponse = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Failure -> throw GentooException(authResponse.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> authResponse.value
        }
    }

    private suspend fun getFloatingComment(itemId: String, userId: String): FloatingCommentResponse {
        val floatingCommentRequest = FloatingCommentRequest(itemId, userId)
        return when (val floatingCommentResponse = apiClient.send(floatingCommentRequest, FloatingCommentResponse.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingCommentResponse.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingCommentResponse.value
        }
    }

    private suspend fun getFloatingProduct(itemId: String, userId: String, target: String): FloatingProductResponse {
        val floatingProductRequest = FloatingProductRequest(itemId, userId, target)
        return when (val floatingProductResponse = apiClient.send(floatingProductRequest, FloatingProductResponse.serializer())) {
            is GentooResponse.Failure -> throw GentooException(floatingProductResponse.errorResponse.error) // TODO : double check how to handle this case
            is GentooResponse.Success -> floatingProductResponse.value
        }
    }
}
