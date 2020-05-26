package com.youloveevents.network

import com.google.gson.annotations.SerializedName

class ApiDiscovery(
    @SerializedName("_embedded") val embedded: ApiEmbedded?,
    @SerializedName("page") val page: ApiPage
)

class ApiPage(
    val size: Int
)

class ApiEmbedded(
    val events: List<ApiEvents>
)

class ApiEvents(
    val name: String,
    val id: String,
    val images: List<ApiEventImage>? = null,
    val sales: ApiEventSalePublic? = null,
    val dates: ApiEventDate? = null,
    val promoter: ApiEventPromoter? = null,
    val url: String? = null
)

class ApiEventImage(
    val ratio: String,
    val url: String,
    val width: Int,
    val height: Int
)

class ApiEventDate(
    var start: ApiEventStartDate,
    val timezone: String
)

class ApiEventStartDate(
    val dateTime: String
)

class ApiEventPromoter(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null
)

class ApiEventSalePublic(
    val public: ApiEventSale? = null
)

class ApiEventSale(
    val startDateTime: String? = null,
    val endDateTime: String? = null
)