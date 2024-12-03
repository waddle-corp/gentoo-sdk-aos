package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.internal.api.APPLICATION_JSON
import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.PostRequest
import com.waddle.gentoo.internal.util.Constants
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class UserEventRequest(
    private val userEvent: UserEvent,
    private val chatUserId: String,
    private val partnerId: String,
    private val channelId: String = "Android"
) : PostRequest {
    override val url: String
        get() = Endpoints.USER_EVENT
    override val headers: Map<String, String>
        get() = emptyMap()
    override val requestBody: RequestBody
        get() = buildJsonObject {
            put("eventCategory", userEvent.userEventCategory.asString)
            put("chatUserId", chatUserId)
            put("partnerId", partnerId)
            put("channelId", channelId)

            when (userEvent) {
                is UserEvent.PurchaseComplete -> {
                    put("products", Constants.json.encodeToString(userEvent.products))
                }

                is UserEvent.AddToCart -> {
                    put("products", Constants.json.encodeToString(userEvent.products))
                }

                UserEvent.SdkFloatingClicked -> {}
                UserEvent.SdkFloatingRendered -> {}
            }
        }.toString().toRequestBody(APPLICATION_JSON)
}

internal enum class UserEventCategory(val asString: String) {
    SDK_FLOATING_RENDERED("SDKFloatingRendered"),
    SDK_FLOATING_CLICKED("SDKFloatingClicked"),
    PURCHASE_COMPLETE("purchase-complete"),
    ADD_TO_CART("add-to-cart")
}

sealed class UserEvent(
    internal val userEventCategory: UserEventCategory
) {
    internal data object SdkFloatingRendered : UserEvent(UserEventCategory.SDK_FLOATING_RENDERED)
    internal data object SdkFloatingClicked : UserEvent(UserEventCategory.SDK_FLOATING_CLICKED)
    data class PurchaseComplete(val products: List<Product> = emptyList()) : UserEvent(UserEventCategory.PURCHASE_COMPLETE)
    data class AddToCart(val products: List<Product> = emptyList()) : UserEvent(UserEventCategory.ADD_TO_CART)
}

@Serializable
data class Product(
    val itemId: String,
    val quantity: Int
)
