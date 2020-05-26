package com.youloveevents.main

import android.net.Uri
import com.youloveevents.database.EventsDBEntity
import com.youloveevents.network.ApiDiscovery
import com.youloveevents.network.ApiEventDate
import com.youloveevents.network.ApiEventImage
import com.youloveevents.network.ApiEventSalePublic
import com.youloveevents.util.UrlUtil
import javax.inject.Inject

class EventsMapper @Inject constructor(
    private val urlUtil: UrlUtil
) {

    fun mapApi(apiDiscovery: ApiDiscovery): List<Event> {
        if (apiDiscovery.page.size <= 0 || apiDiscovery.embedded == null) return emptyList()
        else {
            val events = mutableListOf<Event>()
            apiDiscovery.embedded.events.forEach {
                events.add(Event(
                        name = it.name,
                        id = it.id,
                        smallImage = getSmallImage(it.images),
                        bigImage = getBigImage(it.images),
                        url = it.url?.let { uri -> urlUtil.toUri(uri) },
                        promoter = EventPromoter(it.promoter?.name, it.promoter?.description),
                        sale = getEventSale(it.sales),
                        date = getEventDate(it.dates)
                ))
            }
            return events
        }
    }

    fun mapEvent(event: Event): EventsDBEntity {
        return EventsDBEntity(
                id = event.id,
                name = event.name,
                url = event.url.toString(),
                promoterName = event.promoter.name,
                promoterDescription = event.promoter.description,
                smallImage = if (event.smallImage is EventImage.Data) event.smallImage.uri.toString() else "",
                bigImage = if (event.bigImage is EventImage.Data) event.bigImage.uri.toString() else "",
                startToSale = if (event.sale is EventSale.Data) event.sale.start else "",
                endToSale = if (event.sale is EventSale.Data) event.sale.end else "",
                date = if (event.date is EventDate.Data) event.date.start else "",
                timezone = if (event.date is EventDate.Data) event.date.timezone else ""
        )
    }

    fun mapDbEvents(events: List<EventsDBEntity>): List<Event> {
        return events.map { map(it) }
    }

    private fun map(event: EventsDBEntity): Event {
        return Event(
                id = event.id,
                name = event.name,
                smallImage = if (event.smallImage != null) EventImage.Data(urlUtil.toUri(event.smallImage)) else EventImage.None,
                bigImage = if (event.bigImage != null) EventImage.Data(urlUtil.toUri(event.bigImage)) else EventImage.None,
                url = urlUtil.toUri(event.url),
                promoter = EventPromoter(event.promoterName, event.promoterDescription),
                sale = getEventSale(event.startToSale, event.endToSale),
                date = getEventDate(event.date, event.timezone)
        )
    }

    private fun getEventSale(saleStart: String?, saleEnd: String?): EventSale {
        if (saleStart != null && saleEnd != null) {
            return EventSale.Data(saleStart, saleEnd)
        }
        return EventSale.None
    }

    private fun getEventDate(date: String?, timezone: String?): EventDate {
        if (date != null && timezone != null) {
            return EventDate.Data(date, timezone)
        }
        return EventDate.None
    }

    private fun getSmallImage(images: List<ApiEventImage>?): EventImage {
        images?.forEach {
            if (it.ratio == "16_9" && it.width <= 100 && it.height <= 100) {
                return EventImage.Data(urlUtil.toUri(it.url))
            }
        }
        return EventImage.None
    }

    private fun getBigImage(images: List<ApiEventImage>?): EventImage {
        images?.forEach {
            if (it.ratio == "3_2" && (it.width in 200..400) && (it.height in 200..400)) {
                return EventImage.Data(urlUtil.toUri(it.url))
            }
        }
        return EventImage.None
    }

    private fun getEventSale(date: ApiEventSalePublic?): EventSale {
        return date?.let {
            when {
                it.public?.startDateTime != null
                        && it.public.endDateTime != null -> EventSale.Data(
                        it.public.startDateTime,
                        it.public.endDateTime)
                else -> EventSale.None
            }
        } ?: EventSale.None
    }

    private fun getEventDate(date: ApiEventDate?): EventDate {
        return date?.let {
            EventDate.Data(it.start.dateTime, it.timezone)
        } ?: EventDate.None
    }
}