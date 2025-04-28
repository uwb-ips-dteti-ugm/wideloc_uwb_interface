package com.rizqi.wideloc.data.network

import com.tinder.scarlet.websocket.okhttp.request.RequestFactory
import okhttp3.Request
import javax.inject.Inject

class WebSocketRequestFactory @Inject constructor(
    private val savedAddresses: String,
) : RequestFactory{
    override fun createRequest(): Request {
        return Request.Builder().url(savedAddresses).build()
    }
}