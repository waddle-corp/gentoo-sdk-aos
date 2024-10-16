package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.internal.api.APPLICATION_JSON
import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.PostRequest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class UserEventRequest(
    private val userEventCategory: UserEventCategory,
    private val userId: String,
    private val clientId: String,
    private val itemId: String? = null,
    private val channelId: String = "Android"
) : PostRequest {
    override val url: String
        get() = Endpoints.USER_EVENT
    override val headers: Map<String, String>
        get() = emptyMap()
    override val requestBody: RequestBody
        get() = buildJsonObject {
            put("event_category", userEventCategory.asString)
            put("visitorId", userId)
            put("itemId", itemId ?: "general")
            put("clientId", clientId)
            put("channelId", channelId)
        }.toString().toRequestBody(APPLICATION_JSON)
}

internal enum class UserEventCategory(val asString: String) {
    SDK_FLOATING_RENDERED("SDKFloatingRendered"),
    SDK_FLOATING_CLICKED("SDKFloatingClicked")
}
