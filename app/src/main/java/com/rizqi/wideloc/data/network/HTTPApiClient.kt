package com.rizqi.wideloc.data.network

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.rizqi.wideloc.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class HTTPApiClient(
    context: Context,
    private val baseUrl: String = Constants.ESP_BASE_URL
) {

    companion object {
        private const val TAG = "HTTPApiClient"
    }

    // OkHttpClient with Chucker Interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(ChuckerInterceptor.Builder(context).build())
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun get(endpoint: String, param: List<Pair<String, Any>> = listOf()): String =
        withContext(Dispatchers.IO) {
            val query = param.joinToString("&") { (key, value) ->
                "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value.toString(), "UTF-8")}"
            }
            val urlWithParams = "$baseUrl$endpoint?$query"

            Log.d(TAG, "🔵 [GET] URL: $urlWithParams")

            val request = Request.Builder()
                .url(urlWithParams)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                Log.d(TAG, "✅ [GET] Code: ${response.code}")
                Log.d(TAG, "📥 [GET] Body: $body")

                if (response.isSuccessful) body else throw Exception("GET failed: ${response.code}")
            }
        }

    suspend fun post(endpoint: String, body: String? = null): String =
        withContext(Dispatchers.IO) {
            val url = "$baseUrl$endpoint"
            Log.d(TAG, "🟡 [POST] URL: $url")
            Log.d(TAG, "📤 [POST] Body: $body")

            val requestBody = body?.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .post(requestBody ?: ByteArray(0).toRequestBody())
                .build()

            client.newCall(request).execute().use { response ->
                val responseText = response.body?.string().orEmpty()
                Log.d(TAG, "✅ [POST] Code: ${response.code}")
                Log.d(TAG, "📥 [POST] Body: $responseText")

                if (response.isSuccessful) responseText else throw Exception("POST failed: ${response.code}")
            }
        }
}
