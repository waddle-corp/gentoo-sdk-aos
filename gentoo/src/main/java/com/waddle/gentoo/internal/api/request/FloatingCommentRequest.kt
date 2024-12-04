package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.CommentType
import com.waddle.gentoo.DisplayLocation
import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.GetRequest
import com.waddle.gentoo.internal.util.urlEncoded

internal class FloatingCommentRequest(
    partnerId: String,
    private val floatingCommentData: FloatingCommentData
) : GetRequest {
    override val url: String = Endpoints.FLOATING.format(partnerId.urlEncoded)
    override val headers: Map<String, String>
        get() = emptyMap()
    override val params: Map<String, String>
        get() {
            return mutableMapOf("displayLocation" to floatingCommentData.displayLocation.toString()).apply {
                when (floatingCommentData) {
                    is FloatingCommentData.Home -> {}
                    is FloatingCommentData.ProductList -> {}
                    is FloatingCommentData.ProductDetail -> {
                        floatingCommentData.itemId?.let { this["itemId"] = it }
                        this["commentType"] = floatingCommentData.commentType.asString
                    }
                }
            }
        }
}

internal sealed class FloatingCommentData {
    abstract val displayLocation: DisplayLocation
    internal data object Home: FloatingCommentData() {
        override val displayLocation: DisplayLocation
            get() = DisplayLocation.HOME
    }

    internal data object ProductList: FloatingCommentData() {
        override val displayLocation: DisplayLocation
            get() = DisplayLocation.PRODUCT_LIST
    }

    internal data class ProductDetail(val itemId: String?, val commentType: CommentType): FloatingCommentData() {
        override val displayLocation: DisplayLocation
            get() = DisplayLocation.PRODUCT_DETAIL
    }
}
