package com.rizqi.wideloc.data.network

import android.content.Context
import com.rizqi.wideloc.utils.replaceHostAddress
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URI

class WebSocketInterceptor(
    private val context: Context
) : Interceptor {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface WebSocketModuleEntryPoint {
        fun getSavedAddresses(): String
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val savedAddresses = getSavedAddresses()
        val savedUris = URI(savedAddresses)
        val requestUrl = request.url
        val newAddresses =
            requestUrl.toString().replaceHostAddress(savedUris.host, savedUris.port.toString())
        return chain.proceed(
            request.newBuilder()
                .url(newAddresses.toHttpUrlOrNull() ?: request.url).build()
        )
    }

    private fun getSavedAddresses(): String {
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(context, WebSocketModuleEntryPoint::class.java)
        return hiltEntryPoint.getSavedAddresses()
    }
}