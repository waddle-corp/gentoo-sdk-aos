package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostRecommendResponse(
    val text: String,
    val example: List<String>,
    val intent: String,
    val dialogueId: String,
    val userGroup: String,
    val product: List<Product>
)

@Serializable
data class Product(
    val type: String,
    val serviceType: String,
    val imageUrl: String,
    val itemId: String,
    val name: String,
    val productUrl: String,
    val originPrice: Int,
    val price: Int,
    val discount: Int,
    val rate: Double,
    val reviewNum: Int,
    val shortDesc: String,
    val tags: List<Tag>
)

@Serializable
data class Tag(
    val name: String,
    @SerialName("text_color")
    val textColor: String,
    @SerialName("background_color")
    val backgroundColor: String,
    @SerialName("border_color")
    val borderColor: String
)