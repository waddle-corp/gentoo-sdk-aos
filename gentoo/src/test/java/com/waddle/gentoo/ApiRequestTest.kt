package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.util.GentooResponse
import org.junit.Test

class ApiRequestTest {
    @Test
    fun auth() {
        val apiClient = ApiClient("G4J2wPnd643wRoQiK52PO9ZAtaD6YNCAhGlfm1Oc","https://hg5eey52l4.execute-api.ap-northeast-2.amazonaws.com/dev")
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
}
