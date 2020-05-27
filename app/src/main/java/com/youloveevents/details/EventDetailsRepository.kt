package com.youloveevents.details

import com.youloveevents.database.EventsDatabase
import com.youloveevents.main.Event
import com.youloveevents.EventsMapper
import io.reactivex.Single
import javax.inject.Inject

class EventDetailsRepository @Inject constructor(
    private val mapper: EventsMapper,
    private val database: EventsDatabase
) {

    fun loadEventDetails(id: String): Single<Event> {
        return Single.just(database.eventsDao().getEvent(id))
                .map { mapper.mapDbEvent(it) }
    }
}