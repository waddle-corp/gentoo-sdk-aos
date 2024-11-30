package com.waddle.gentoo.internal.api.request

import com.waddle.gentoo.CommentType
import com.waddle.gentoo.DisplayLocation
import com.waddle.gentoo.internal.api.Endpoints
import com.waddle.gentoo.internal.api.GetRequest
import com.waddle.gentoo.internal.util.urlEncoded

internal class FloatingCommentRequest(
    partnerId: String,
    private val floatingData: FloatingData
) : GetRequest {
    override val url: String = Endpoints.FLOATING.format(partnerId.urlEncoded)
    override val headers: Map<String, String>
        get() = emptyMap()
    override val params: Map<String, String>
        get() {
            return mutableMapOf("displayLocation" to floatingData.displayLocation.toString()).apply {
                when (floatingData) {
                    is FloatingData.Home -> {}
                    is FloatingData.ProductList -> {}
                    is FloatingData.ProductDetail -> {
                        this["itemId"] = floatingData.itemId
                        this["commentType"] = floatingData.commentType.asString
                    }
                }
            }
        }
}

internal sealed class FloatingData {
    abstract val displayLocation: DisplayLocation
    internal data object Home: FloatingData() {
        override val displayLocation: DisplayLocation
            get() = DisplayLocation.HOME
    }

    internal data object ProductList: FloatingData() {
        override val displayLocation: DisplayLocation
            get() = DisplayLocation.PRODUCT_LIST
    }

    internal data class ProductDetail(val itemId: String, val commentType: CommentType): FloatingData() {
        override val displayLocation: DisplayLocation
            get() = DisplayLocation.PRODUCT_DETAIL
    }
}
