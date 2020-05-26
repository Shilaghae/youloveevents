package com.youloveevents.util

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class DateTimeUtil @Inject constructor() {

    private val dayOfMonthMonthAndYearFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    fun toLocalDate(dateTime: String): LocalDate = parseISODate(dateTime).toLocalDate()

    fun parseISODate(date: String?): ZonedDateTime = parseISODate(date, null)

    fun parseISODate(date: String?, timezone: String?): ZonedDateTime =
            ZonedDateTime.parse(date).withZoneSameInstant(timezone.toZoneId())

    private fun String?.toZoneId(): ZoneId = if (this != null) ZoneId.of(this) else ZoneId.systemDefault()

    fun formatDayOfMonthMonthAndYear(dateTime: ZonedDateTime): String = dateTime.format(dayOfMonthMonthAndYearFormatter)
}