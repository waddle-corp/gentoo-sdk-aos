package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.GetRecommendRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.GetRecommendResponse
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.PostRecommendRequest
import com.waddle.gentoo.internal.api.response.PostRecommendResponse
import io.kotest.assertions.fail
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class ApiRequestsTest {
    val apiClient = ApiClient(
        "G4J2wPnd643wRoQiK52PO9ZAtaD6YNCAhGlfm1Oc",
        "https://hg5eey52l4.execute-api.ap-northeast-2.amazonaws.com/dev"
    )

    @Test
    fun auth() = runBlocking {
        val authRequest = AuthRequest(testUdid, testAuthCode)
        val response = apiClient.send(authRequest, AuthResponse.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<AuthResponse>>()
        Unit
    }

    @Test
    fun getRecommend() = runBlocking {
        val authRequest = AuthRequest(testUdid, testAuthCode)
        val userId = when (val response = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Success -> response.value.body.randomId
            is GentooResponse.Failure -> fail("AuthRequest should not fail")
        }

        val getRecommendRequest = GetRecommendRequest(testItemId, userId)
        val response = apiClient.send(getRecommendRequest, GetRecommendResponse.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<GetRecommendResponse>>()
        Unit
    }

    @Test
    fun postRecommend() = runBlocking {
        val authRequest = AuthRequest(testUdid, testAuthCode)
        val userId = when (val response = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Success -> response.value.body.randomId
            is GentooResponse.Failure -> fail("AuthRequest should not fail")
        }

        val thisRequest = PostRecommendRequest(testItemId, userId, "this")
        var response = apiClient.send(thisRequest, PostRecommendResponse.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<PostRecommendResponse>>()

        val needsRequest = PostRecommendRequest(testItemId, userId, "needs")
        response = apiClient.send(needsRequest, PostRecommendResponse.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<PostRecommendResponse>>()
        Unit
    }

    companion object {
        const val testItemId = "3190"
        const val testUdid = "test"
        const val testAuthCode = "test"
    }
}

