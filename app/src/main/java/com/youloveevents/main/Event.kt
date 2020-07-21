package com.youloveevents.main

import android.net.Uri

class Event(
    val name: String,
    val id: String,
    val smallImage: EventImage,
    val bigImage: EventImage,
    val promoter: EventPromoter,
    val url: Uri?,
    val sale: EventSale,
    val date: EventDate
)
class EventPromoter(
    val name: String?,
    val description: String?
)

sealed class EventImage {
    object None : EventImage()
    data class Data(val uri: Uri) : EventImage()
}

sealed class EventSale {
    object None : EventSale()
    data class Data(val start: String, val end: String) : EventSale()
}

sealed class EventDate {
    object None : EventDate()
    data class Data(val start: String, val timezone: String?) : EventDate()
}