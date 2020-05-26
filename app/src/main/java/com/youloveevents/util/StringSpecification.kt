package com.youloveevents.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class StringSpecification {
    data class Text(val value: String) : StringSpecification()
    object Null : StringSpecification()
    class Resource(@StringRes val value: Int, vararg val args: Any) : StringSpecification()
    class Plural(@PluralsRes val value: Int, val quantity: Int, vararg val args: Any) : StringSpecification() {
        constructor(@PluralsRes value: Int, quantity: Int) : this(value, quantity, quantity)
    }
}

fun Context.resolve(specification: StringSpecification): String {
    return when (specification) {
        is StringSpecification.Text -> specification.value
        is StringSpecification.Null -> ""
        is StringSpecification.Resource -> getString(specification.value, *specification.args)
        is StringSpecification.Plural -> resources.getQuantityString(specification.value, specification.quantity, *specification.args)
    }
}
