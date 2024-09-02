package com.waddle.gentoo.internal.api

import com.waddle.gentoo.BuildConfig
import com.waddle.gentoo.internal.exception.GentooException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

internal class ApiClient(
    private val apiKey: String,
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

    fun <T> send(request: ApiRequest, responseHandler: ResponseHandler<T>?) {
        okHttpClient.newCall(buildRequest(request)).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    responseHandler?.onResponse(
                        com.waddle.gentoo.internal.util.Response.Failure(
                            GentooException(e)
                        )
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        // TODO add some ErrorResponse and call response handler as failure
                        return
                    }

                    val body = response.body
                    // TODO kotlin serialization
                }
            }
        )
    }


    private fun buildRequest(apiRequest: ApiRequest): Request {
        val request = Request.Builder()
            .addHeader("x-api-key", apiKey)

        apiRequest.headers.forEach {
            request.addHeader(it.key, it.value)
        }

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
