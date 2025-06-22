package com.rizqi.wideloc.data.network

import android.util.Log
import com.rizqi.wideloc.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HTTPApiClient(private val baseUrl: String = Constants.ESP_BASE_URL) {

    companion object {
        private const val TAG = "HTTPApiClient"
    }

    suspend fun get(endpoint: String, param: List<Pair<String, Any>>): String = withContext(Dispatchers.IO) {
        val query = param.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value.toString(), "UTF-8")}"
        }
        val urlWithParams = "$baseUrl$endpoint?$query"
        val url = URL(urlWithParams)
        val connection = url.openConnection() as HttpURLConnection

        Log.d(TAG, "🔵 [GET] URL: $urlWithParams")

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            val responseText = connection.inputStream.bufferedReader().use(BufferedReader::readText)

            Log.d(TAG, "✅ [GET] Response Code: $responseCode")
            Log.d(TAG, "📥 [GET] Response Body: $responseText")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseText
            } else {
                throw Exception("GET request failed with code $responseCode")
            }
        } catch (e: Exception) {
            val errorStream = connection.errorStream?.bufferedReader()?.use(BufferedReader::readText)
            Log.e(TAG, "❌ [GET] Request failed: ${e.message}")
            if (errorStream != null) {
                Log.e(TAG, "❌ [GET] Error Body: $errorStream")
            }
            throw e
        } finally {
            connection.disconnect()
        }
    }

    suspend fun post(endpoint: String, body: String): String = withContext(Dispatchers.IO) {
        val url = URL("http://$baseUrl$endpoint")
        val connection = url.openConnection() as HttpURLConnection

        Log.d(TAG, "🟡 [POST] URL: $url")
        Log.d(TAG, "📤 [POST] Request Body: $body")

        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            connection.outputStream.use { os: OutputStream ->
                os.write(body.toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = connection.responseCode
            val responseText = connection.inputStream.bufferedReader().use(BufferedReader::readText)

            Log.d(TAG, "✅ [POST] Response Code: $responseCode")
            Log.d(TAG, "📥 [POST] Response Body: $responseText")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseText
            } else {
                throw Exception("POST request failed with code $responseCode")
            }
        } catch (e: Exception) {
            val errorStream = connection.errorStream?.bufferedReader()?.use(BufferedReader::readText)
            Log.e(TAG, "❌ [POST] Request failed: ${e.message}")
            if (errorStream != null) {
                Log.e(TAG, "❌ [POST] Error Body: $errorStream")
            }
            throw e
        } finally {
            connection.disconnect()
        }
    }
}
