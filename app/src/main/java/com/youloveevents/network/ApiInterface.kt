package com.youloveevents.network

import io.reactivex.Single
import retrofit2.http.GET

interface ApiInterface {

    @GET("/discovery/v2/events.json")
    fun events(): Single<ApiDiscovery>
}