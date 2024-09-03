package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.PostRequest
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class AuthRequest(
    private val udid: String,
    private val authCode: String,
) : PostRequest {
    override val requestBody: RequestBody
        get() = "".toRequestBody()
    override val url: String
        get() = Endpoints.AUTH
    override val headers: Map<String, String>
        get() = mapOf(
            "udid" to udid,
            "authCode" to authCode,
            "Content-Type" to "application/json"
        )
    override val isApiKeyRequired: Boolean = true
}
