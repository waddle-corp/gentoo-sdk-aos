package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.GetRequest

internal class FloatingCommentRequest(
    val itemId: String,
    val userId: String
) : GetRequest {
    override val url: String = Endpoints.RECOMMEND
    override val headers: Map<String, String>
        get() = emptyMap()
    override val params: Map<String, String>
        get() = mapOf(
            "itemId" to itemId,
            "userId" to userId
        )
}
