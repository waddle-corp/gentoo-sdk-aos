package com.waddle.gentoo

enum class CommentType(internal val asString: String) {
    DEFAULT("default"),
    THIS("this"),
    NEEDS("needs")
}

enum class DisplayLocation {
    HOME, PRODUCT_LIST, PRODUCT_DETAIL
}