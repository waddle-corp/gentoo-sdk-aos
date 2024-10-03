package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FloatingProduct(
    val text: String,
    val example: List<String>,
    val intent: String,
    val dialogueId: String,
    val userGroup: String,
    val product: List<Product>
)

@Serializable
internal data class Product(
    val type: String,
    val serviceType: String,
    val imageUrl: String,
    val itemId: Int,
    val name: String,
    val productUrl: String,
    val originPrice: Int,
    val price: Int,
    val discount: Int,
    val rate: Double,
    val reviewNum: Int,
    val shortDesc: String? = null,
    val tags: List<Tag>
)

@Serializable
internal data class Tag(
    val name: String,
    @SerialName("text_color")
    val textColor: String,
    @SerialName("background_color")
    val backgroundColor: String,
    @SerialName("border_color")
    val borderColor: String
)