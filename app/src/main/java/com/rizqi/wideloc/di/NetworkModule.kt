package com.rizqi.wideloc.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebsocketHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultHttpClient
object NetworkModule {
}