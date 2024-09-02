package com.waddle.gentoo.internal.api

import com.waddle.gentoo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

internal class ApiClient(
    private val baseUrl: String
) {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                // Print http log only when it is debug mode for security reason.
                val level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                setLevel(level)
            }
        )
        .build()


    fun buildRequest(apiRequest: ApiRequest): Request {
        val request = Request.Builder() // TODO : add headers here
        return when (apiRequest) {
            is GetRequest -> {
                request.url(baseUrl + apiRequest.getQueryUrl())
                    .get()
                    .build()
            }

            is PostRequest -> {
                request.url(baseUrl)
                    .post(apiRequest.requestBody)
                    .build()
            }

            is PutRequest -> {
                request.url(baseUrl)
                    .put(apiRequest.requestBody)
                    .build()
            }

            is DeleteRequest -> {
                request.url(apiRequest.getQueryUrl())
                    .delete(apiRequest.requestBody)
                    .build()
            }
        }
    }
}
