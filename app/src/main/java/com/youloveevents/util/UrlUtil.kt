package com.youloveevents.util

import android.net.Uri
import javax.inject.Inject

class UrlUtil @Inject constructor() {

    fun toUri(uri: String) : Uri {
        return Uri.parse(uri)
    }
}