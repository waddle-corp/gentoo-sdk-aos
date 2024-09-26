package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.ChatType
import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.GetRequest
import com.waddle.gentoo.internal.util.urlEncoded

internal class FloatingCommentRequest(
    clientId: String,
    val itemId: String,
    val userId: String,
    val commentType: ChatType
) : GetRequest {
    override val url: String = Endpoints.RECOMMEND.format(clientId.urlEncoded)
    override val headers: Map<String, String>
        get() = emptyMap()
    override val params: Map<String, String>
        get() = mapOf(
            "itemId" to itemId,
            "userId" to userId,
            "commentType" to commentType.asString
        )
}
