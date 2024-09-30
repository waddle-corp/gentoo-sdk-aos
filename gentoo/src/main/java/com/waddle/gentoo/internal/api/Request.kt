package com.waddle.gentoo.internal.api

import com.waddle.gentoo.internal.util.urlEncoded
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

internal sealed interface ApiRequest {
    val url: String
    val headers: Map<String, String>
    val isApiKeyRequired: Boolean
        get() = false
}

internal interface GetRequest : ApiRequest {
    val params: Map<String, String>?
        get() = null

    fun getQueryUrl(): String {
        return params?.let { "$url?${it.queryString}" } ?: url
    }
}

internal interface PostRequest : ApiRequest {
    val requestBody: RequestBody
}

internal interface PutRequest : ApiRequest {
    val requestBody: RequestBody
}

internal interface DeleteRequest : ApiRequest {
    val params: Map<String, String>?
        get() = null

    val requestBody: RequestBody?
        get() = null

    fun getQueryUrl(): String {
        return params?.let { "$url?${it.queryString}" } ?: url
    }
}

private val Map<String, String>.queryString: String
    get() = this.entries.joinToString(separator = "&") { "${it.key.urlEncoded}=${it.value.urlEncoded}" }

internal val APPLICATION_JSON : MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()