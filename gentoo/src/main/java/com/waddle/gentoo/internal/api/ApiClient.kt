package com.waddle.gentoo.internal.api

import androidx.annotation.WorkerThread
import com.waddle.gentoo.Logger
import com.waddle.gentoo.internal.api.response.ErrorResponse
import com.waddle.gentoo.internal.exception.GentooException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class ApiClient(
    private val baseUrl: String
) {
    val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.NONE)
    }
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    @WorkerThread
    @Throws(GentooException::class)
    suspend fun <T> send(request: ApiRequest, serializer: KSerializer<T>): GentooResponse<T> {
        try {
            val response = okHttpClient.newCall(request.toRequest()).await()
            val statusCode = response.code
            val body = response.body?.string()
                ?: return GentooResponse.Failure(ErrorResponse(statusCode, "Body should not be empty. request = ${request::class}"))

            try {
                if (!response.isSuccessful) {
                    return GentooResponse.Failure(ErrorResponse(statusCode, body))
                }

                val result = json.decodeFromString(serializer, body)
                return GentooResponse.Success(result)
            } catch (e: Exception) {
                throw GentooException(e)
            }
        } catch (e: Exception) {
            Logger.e("Failed to send request: $request")
            throw GentooException(e)
        }
    }

    private fun ApiRequest.toRequest(): Request {
        val request = Request.Builder()

        this.headers.forEach {
            request.addHeader(it.key, it.value)
        }

        return when (this) {
            is GetRequest -> {
                request.url(baseUrl + this.getQueryUrl())
                    .get()
                    .build()
            }

            is PostRequest -> {
                request.url(baseUrl + this.url)
                    .post(this.requestBody)
                    .build()
            }

            is PutRequest -> {
                request.url(baseUrl + this.url)
                    .put(this.requestBody)
                    .build()
            }

            is DeleteRequest -> {
                request.url(baseUrl + this.getQueryUrl())
                    .delete(this.requestBody)
                    .build()
            }
        }
    }

    internal suspend fun Call.await() = suspendCancellableCoroutine { continuation ->
        this.enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            }
        )
    }
}
