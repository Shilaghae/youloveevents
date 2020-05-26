package com.youloveevents.main

import com.youloveevents.R
import com.youloveevents.error.SimpleDomainException
import com.youloveevents.util.DateTimeUtil
import com.youloveevents.util.Lce
import com.youloveevents.util.StringSpecification
import io.reactivex.Observable
import javax.inject.Inject

class EventListInteractor @Inject constructor(
    private val repository: EventListRepository,
    private val dateTimeUtil: DateTimeUtil
) {

    fun loadEvent(): Observable<Lce<List<UiEvent>>> {
        return repository.getEvents()
                .map { convert(it) }
                .startWith(Lce.Loading)
                .onErrorReturn {
                    Lce.Error(throwable = SimpleDomainException("Error while loading the events ${it}"))
                }
    }

    private fun convert(events: List<Event>): Lce<List<UiEvent>> {
        val e = mutableListOf<UiEvent>()
        events.forEach {
            val timezone = if (it.date is EventDate.Data) it.date.timezone else null
            e.add(UiEvent(
                    name = it.name,
                    smallImage = if (it.smallImage is EventImage.Data) {
                        UiEventImage.Data(uri = it.smallImage.uri)
                    } else {
                        UiEventImage.Void
                    },
                    bigImage = if (it.bigImage is EventImage.Data) {
                        UiEventImage.Data(uri = it.bigImage.uri)
                    } else {
                        UiEventImage.Void
                    },
                    url = it.url,
                    timezone = timezone,
                    sale = if (it.sale is EventSale.Data) {
                        val from = dateTimeUtil.formatDayOfMonthMonthAndYear(dateTimeUtil.parseISODate(it.sale.start, timezone))
                        val to = dateTimeUtil.formatDayOfMonthMonthAndYear(dateTimeUtil.parseISODate(it.sale.end, timezone))
                        StringSpecification.Resource(R.string.event_sale, from, to)
                    } else StringSpecification.Null,
                    promoter = if (it.promoter.name != null) StringSpecification.Text(it.promoter.name) else StringSpecification.Null
            ))
        }
        return Lce.Data(e)
    }
}