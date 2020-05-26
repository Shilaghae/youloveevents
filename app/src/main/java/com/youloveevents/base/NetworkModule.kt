package com.youloveevents.base

import com.google.gson.GsonBuilder
import com.youloveevents.network.ApiInterface
import com.youloveevents.network.ApiKeyInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideApiInterface(
        @Named(API_TICKETMASTER_ENDPOINT) baseUrl: HttpUrl,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): ApiInterface {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiInterface::class.java)
    }

    @Provides
    @Named(API_TICKETMASTER_ENDPOINT)
    fun provideTicketMasterEndpoint(): HttpUrl {
        return API_TICKETMASTER.toHttpUrlOrNull()!!
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create(GsonBuilder().create())

    @Provides
    @Singleton
    fun provideApiClient(
        apiInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(apiInterceptor)
                .build()
    }
}

const val API_TICKETMASTER = "https://app.ticketmaster.com"
const val API_TICKETMASTER_ENDPOINT = "ticketmaster endpoint"