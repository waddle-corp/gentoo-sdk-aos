package com.waddle.gentoo

enum class CommentType(internal val asString: String) {
    DEFAULT("default"),
    THIS("this"),
    NEEDS("needs")
}

enum class FloatingActionButtonType(internal val asString: String) {
    DEFAULT("default"),
    DETAIL("detail")
}

enum class DisplayLocation {
    HOME, PRODUCT_LIST, PRODUCT_DETAIL
}