package com.youloveevents.main

import android.net.Uri
import com.youloveevents.util.StringSpecification

data class UiEvent(
    val id: String,
    val name: String,
    val smallImage: UiEventImage,
    val bigImage: UiEventImage,
    val url: Uri?,
    val timezone: String?,
    val sale: StringSpecification,
    val promoter: StringSpecification,
    val promoterDescription: StringSpecification
)

sealed class UiEventImage {
    object Void : UiEventImage()
    class Data(val uri: Uri) : UiEventImage()
}