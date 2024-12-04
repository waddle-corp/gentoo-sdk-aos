package com.waddle.gentoo

import com.waddle.gentoo.internal.api.ApiClient
import com.waddle.gentoo.internal.api.GentooResponse
import com.waddle.gentoo.internal.api.request.AuthRequest
import com.waddle.gentoo.internal.api.request.FloatingCommentData
import com.waddle.gentoo.internal.api.request.FloatingCommentRequest
import com.waddle.gentoo.internal.api.request.FloatingProductRequest
import com.waddle.gentoo.internal.api.request.Product
import com.waddle.gentoo.internal.api.request.UserEvent
import com.waddle.gentoo.internal.api.request.UserEventRequest
import com.waddle.gentoo.internal.api.response.AuthResponse
import com.waddle.gentoo.internal.api.response.FloatingComment
import com.waddle.gentoo.internal.api.response.FloatingProduct
import com.waddle.gentoo.internal.api.response.UserEventResponse
import com.waddle.gentoo.internal.util.Constants
import io.kotest.assertions.fail
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import org.junit.Test

internal class UserEventRequestTest {
    val mainApiClient = ApiClient(Constants.MAIN_SERVER_URL)
    val apiClient = ApiClient(Constants.USER_EVENT_SERVER_URL)

    @Test
    fun test_purchase_completed() = runBlocking {
        val products = listOf(Product("111", 1), Product("222", 2))
        println(Constants.json.encodeToString(products))
        val authRequest = AuthRequest(testUdid, testUserToken)
        val response = mainApiClient.send(authRequest, AuthResponse.serializer())
        response.shouldBeTypeOf<GentooResponse.Success<AuthResponse>>()

        val userEvent = UserEvent.PurchaseComplete(listOf(Product("111", 1), Product("222", 2)))
        val userEventRequest = UserEventRequest(userEvent, response.value.chatUserId, testPartnerId)
        val userEventResponse = apiClient.send(userEventRequest, UserEventResponse.serializer())
        println()
        Unit
    }

    companion object {
        const val testPartnerId = "6737041bcf517dbd2b8b6458"
        const val testUserToken = "Token 65ca7bbe5995ac373b06bf3a2c09962a65403245"
        const val testUdid = "d02a7e31-3727-4e72-8768-88d06d313eed"
    }
}

