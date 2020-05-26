package com.youloveevents.main

import android.net.Uri
import com.youloveevents.util.StringSpecification

class UiEvent(
    val name: String,
    val smallImage: UiEventImage,
    val bigImage: UiEventImage,
    val url: Uri?,
    val timezone: String?,
    val sale: StringSpecification,
    val promoter: StringSpecification
)

sealed class UiEventImage {
    object Void : UiEventImage()
    class Data(val uri: Uri) : UiEventImage()
}