package com.waddle.gentoo.internal.api

import android.support.annotation.WorkerThread
import com.waddle.gentoo.BuildConfig
import com.waddle.gentoo.internal.exception.GentooException
import com.waddle.gentoo.internal.util.GentooResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

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

    private val json = Json { ignoreUnknownKeys = true }

    @WorkerThread
    fun <T> send(request: ApiRequest, serializer: KSerializer<T>): GentooResponse<T> {
        try {
            val response = okHttpClient.newCall(buildRequest(request)).execute()
            if (!response.isSuccessful) {
                // TODO add some ErrorResponse and call response handler as failure
                return GentooResponse.Failure(GentooException("Response was unsuccessful"))
            }

            val body = response.body?.string()
            if (body == null) {
                val e = GentooException("Body should not be empty. request = ${request::class}")
                return GentooResponse.Failure(e)
            }
            try {
                val result = json.decodeFromString(serializer, body)
                return GentooResponse.Success(result)
            } catch (e: Exception) {
                return GentooResponse.Failure(GentooException(e))
            }
        } catch (e: Exception) {
            return GentooResponse.Failure(GentooException(e))
        }
    }


    private fun buildRequest(apiRequest: ApiRequest): Request {
        val request = Request.Builder()

        if (apiRequest.isApiKeyRequired) {
            request.addHeader("x-api-key", apiKey)
        }

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
                request.url(baseUrl + apiRequest.url)
                    .post(apiRequest.requestBody)
                    .build()
            }

            is PutRequest -> {
                request.url(baseUrl + apiRequest.url)
                    .put(apiRequest.requestBody)
                    .build()
            }

            is DeleteRequest -> {
                request.url(baseUrl + apiRequest.getQueryUrl())
                    .delete(apiRequest.requestBody)
                    .build()
            }
        }
    }
}
