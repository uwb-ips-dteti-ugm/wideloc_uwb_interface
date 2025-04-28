package com.rizqi.wideloc.di

import android.content.Context
import com.rizqi.wideloc.data.network.WebSocketInterceptor
import com.rizqi.wideloc.data.network.WebSocketRequestFactory
import com.rizqi.wideloc.data.websocket.FlowStreamAdapter
import com.rizqi.wideloc.data.websocket.WideLocSocketService
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {
    @Provides
    fun providesSavedAddresses(): String = ""

    @Provides
    @Singleton
    fun providesWebsocketInterceptor(
        @ApplicationContext context: Context
    ): WebSocketInterceptor = WebSocketInterceptor(context)

    @Provides
    @Singleton
    fun providesScarletLifecycleRegistry(): LifecycleRegistry =
        LifecycleRegistry()

    @Provides
    @Singleton
    fun provideScarlet(
        @WebsocketHttpClient client: OkHttpClient,
        requestFactory: WebSocketRequestFactory,
        lifecycleRegistry: LifecycleRegistry
    ): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(requestFactory))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(FlowStreamAdapter.Factory())
            .lifecycle(lifecycleRegistry)
            .build()
    }

    @Provides
    fun provideWideLocSocketService(scarlet: Scarlet): WideLocSocketService =
        scarlet.create()
}