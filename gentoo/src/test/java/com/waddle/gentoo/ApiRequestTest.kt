package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentData
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingComment
import com.waddle.gentoo.internal.api.response.FloatingProduct
import com.waddle.gentoo.internal.util.Constants
import io.kotest.assertions.fail
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class ApiRequestsTest {
    val apiClient = ApiClient(Constants.MAIN_SERVER_URL)

    @Test
    fun test_auth() = runBlocking {
        val authRequest = AuthRequest(testUdid, testAuthCode)
        val response = apiClient.send(authRequest, AuthResponse.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<AuthResponse>>()
        Unit
    }

    @Test
    fun test_floatingCommentRequest() = runBlocking {
        val floatingCommentRequest = FloatingCommentRequest("6737041bcf517dbd2b8b6458", FloatingCommentData.Home)
        val response = apiClient.send(floatingCommentRequest, FloatingComment.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<FloatingComment>>()
        println("response : $response")

        val floatingCommentRequest2 = FloatingCommentRequest("6737041bcf517dbd2b8b6458", FloatingCommentData.ProductDetail(testItemId, CommentType.THIS))
        val response2 = apiClient.send(floatingCommentRequest2, FloatingComment.serializer())
        println("response2 : $response2")
        response.shouldBeTypeOf<GentooResponse.Success<FloatingComment>>()

        Unit
    }

    @Test
    fun test_floatingProductRequest() = runBlocking {
        val authRequest = AuthRequest(testUdid, testAuthCode)
        val userId = when (val response = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Success -> response.value.chatUserId
            is GentooResponse.Failure -> fail("AuthRequest should not fail")
        }

        val thisRequest = FloatingProductRequest(testItemId, userId, "this")
        var response = apiClient.send(thisRequest, FloatingProduct.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<FloatingProduct>>()

        val needsRequest = FloatingProductRequest(testItemId, userId, "needs")
        response = apiClient.send(needsRequest, FloatingProduct.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<FloatingProduct>>()
        Unit
    }

    companion object {
        const val testItemId = "3190"
        const val testUdid = "test"
        const val testAuthCode = "test"
    }
}

