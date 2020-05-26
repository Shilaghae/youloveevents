package com.youloveevents.network

import com.youloveevents.BuildConfig
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Chain): Response {
        val builder = chain.request().url.newBuilder()
        val url = builder.addQueryParameter(API_KEY, BuildConfig.THETICKETMASTER_API_KEY).build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

private const val API_KEY = "apikey"