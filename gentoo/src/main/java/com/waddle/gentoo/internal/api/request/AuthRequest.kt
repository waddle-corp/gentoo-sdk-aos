package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.GetRequest
import com.waddle.gentoo.internal.api.PostRequest
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class AuthRequest(
    private val udid: String,
    private val userToken: String,
) : GetRequest {
    override val url: String
        get() = Endpoints.USER
    override val headers: Map<String, String>
        get() = mapOf(
            "Content-Type" to "application/json"
        )
    override val params: Map<String, String>
        get() = mapOf(
            "udid" to udid,
            "userToken" to userToken,
        )

    override val isApiKeyRequired: Boolean = false
}
