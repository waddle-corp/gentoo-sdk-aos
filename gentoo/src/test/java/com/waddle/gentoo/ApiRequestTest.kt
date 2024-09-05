package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GetRequest
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.GetRecommendRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.GetRecommendResponse
import com.waddle.gentoo.internal.util.GentooResponse
import org.junit.Assert
import org.junit.Test

internal class ApiRequestsTest {
    val apiClient = ApiClient("G4J2wPnd643wRoQiK52PO9ZAtaD6YNCAhGlfm1Oc","https://hg5eey52l4.execute-api.ap-northeast-2.amazonaws.com/dev")
    @Test
    fun auth() {
        val authRequest = AuthRequest("test", "test")
        when (val response = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Success -> {
                println("${response.value}")
            }
            is GentooResponse.Failure -> {
                println("${response.e}")
            }
        }
    }

    @Test
    fun getRecommend() {
        val authRequest = AuthRequest("test", "test")
        val userId = when (val response = apiClient.send(authRequest, AuthResponse.serializer())) {
            is GentooResponse.Success -> response.value.body.randomId
            is GentooResponse.Failure -> throw response.e
        }

        val getRecommendRequest = GetRecommendRequest("3190", userId)
        when (val response = apiClient.send(getRecommendRequest, GetRecommendResponse.serializer())) {
            is GentooResponse.Success -> {
                println("response: ${response.value}")
            }

            is GentooResponse.Failure -> {
                println("error: ${response.e}")
            }
        }
    }

}
