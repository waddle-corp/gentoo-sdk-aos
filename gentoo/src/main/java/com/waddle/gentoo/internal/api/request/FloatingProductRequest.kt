package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.internal.api.APPLICATION_JSON
import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.PostRequest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class FloatingProductRequest(
    private val itemId: String,
    private val userId: String,
    private val target: String,
    private val channelId: String = "mobile" // TODO : check if it is fixed string
) : PostRequest {
    override val requestBody: RequestBody
        get() = buildJsonObject {
            put("itemId", itemId)
            put("userId", userId)
            put("target", target)
            put("channelId", channelId)
        }.toString().toRequestBody(APPLICATION_JSON)
    override val url: String
        get() = Endpoints.RECOMMEND
    override val headers: Map<String, String>
        get() = emptyMap()
}
