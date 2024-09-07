package com.waddle.gentoo

import com.waddle.gentoo.ApiRequestsTest.Companion.testAuthCode
import com.waddle.gentoo.ApiRequestsTest.Companion.testItemId
import com.waddle.gentoo.ApiRequestsTest.Companion.testUdid
import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingCommentResponse
import com.waddle.gentoo.internal.api.response.PostRecommendResponse
import io.kotest.assertions.fail
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

// TODO(Selen) : write the code that tests api requests and responses.
internal class ApiTest {
    val apiClient = ApiClient(
        "G4J2wPnd643wRoQiK52PO9ZAtaD6YNCAhGlfm1Oc",
        "https://hg5eey52l4.execute-api.ap-northeast-2.amazonaws.com/dev"
    )

    @Test
    fun test_auth() = runBlocking {
        // 1. send an auth request

        // 2. verify response
    }


    @Test
    fun test_floatingCommentRequest() = runBlocking {
        // 1. auth

        // 2. send FloatingCommentRequest

        // 3. verify response
    }

    @Test
    fun test_floatingProductRequest() = runBlocking {
        // 1. auth

        // 2. send FloatingProductRequest with target == "this"

        // 3. verify response

        // 4. send FloatingProductRequest with target == "needs"

        // 5. verify response
    }
}
