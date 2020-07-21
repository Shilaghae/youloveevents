package com.youloveevents.main

import android.util.Log
import com.youloveevents.EventsMapper
import com.youloveevents.database.EventsDatabase
import com.youloveevents.network.ApiInterface
import io.reactivex.Observable
import javax.inject.Inject

class EventListRepository @Inject constructor(
    private val api: ApiInterface,
    private val mapper: EventsMapper,
    private val database: EventsDatabase
) {

    fun loadEvents(): Observable<List<Event>> {
        return api.events()
                .toObservable()
                .map { mapper.mapApi(it) }
                .doOnNext { saveEventsIntoDb(it) }
                .doOnNext { getEventsFromDb() }
                .startWith(getEventsFromDb())
                .onErrorReturn {
                    it.printStackTrace()
                    Log.d(this::class.java.name, it.message!!)
                    getEventsFromDb()
                }
    }

    private fun saveEventsIntoDb(events: List<Event>) {
        database.eventsDao().deleteAllEvents()
        events.map { database.eventsDao().insert(mapper.mapEvent(it)) }
    }

    private fun getEventsFromDb(): List<Event> {
        return mapper.mapDbEvents(database.eventsDao().getAllEvents())
    }
}