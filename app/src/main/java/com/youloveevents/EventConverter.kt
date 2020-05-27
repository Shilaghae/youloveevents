package com.youloveevents

import com.youloveevents.R.string
import com.youloveevents.main.Event
import com.youloveevents.main.EventDate
import com.youloveevents.main.EventImage.Data
import com.youloveevents.main.EventSale
import com.youloveevents.main.UiEvent
import com.youloveevents.main.UiEventImage
import com.youloveevents.main.UiEventImage.Void
import com.youloveevents.util.DateTimeUtil
import com.youloveevents.util.Lce
import com.youloveevents.util.StringSpecification.Null
import com.youloveevents.util.StringSpecification.Resource
import javax.inject.Inject

class EventConverter @Inject constructor(
    private val dateTimeUtil: DateTimeUtil
) {

    fun convertEvents(events: List<Event>): Lce<List<UiEvent>> {
        val e = mutableListOf<UiEvent>()
        events.map { e.add(convert(it)) }
        return Lce.Data(e)
    }

    fun convertEvent(event: Event): Lce<UiEvent> {
        return Lce.Data(convert(event))
    }

    private fun convert(it: Event): UiEvent {
        val timezone = if (it.date is EventDate.Data) it.date.timezone else null
        return UiEvent(
                id = it.id,
                name = it.name,
                smallImage = if (it.smallImage is Data) {
                    UiEventImage.Data(uri = it.smallImage.uri)
                } else {
                    Void
                },
                bigImage = if (it.bigImage is Data) {
                    UiEventImage.Data(uri = it.bigImage.uri)
                } else {
                    Void
                },
                url = it.url,
                timezone = timezone,
                sale = if (it.sale is EventSale.Data) {
                    val from = dateTimeUtil.formatDayOfMonthMonthAndYear(dateTimeUtil.parseISODate(it.sale.start, timezone))
                    val to = dateTimeUtil.formatDayOfMonthMonthAndYear(dateTimeUtil.parseISODate(it.sale.end, timezone))
                    Resource(string.event_sale, from, to)
                } else Null,
                promoter = if (it.promoter.name != null) Resource(string.event_promoter, it.promoter.name) else Null,
                promoterDescription = if (it.promoter.description != null) Resource(string.event_promoter_description, it.promoter.description) else Null
        )
    }
}