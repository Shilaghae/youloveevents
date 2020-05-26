package com.youloveevents.main

import android.util.Log
import com.youloveevents.database.EventsDatabase
import com.youloveevents.network.ApiInterface
import io.reactivex.Observable
import javax.inject.Inject

class EventListRepository @Inject constructor(
    private val api: ApiInterface,
    private val mapper: EventsMapper,
    private val database: EventsDatabase
) {

    fun getEvents(): Observable<List<Event>> {
        return api.events()
                .toObservable()
                .map { mapper.mapApi(it) }
                .doOnNext { saveIntoDB(it) }
                .doOnNext { mapper.mapDbEvents(database.eventsDao().getAllEvents()) }
                .onErrorReturn { mapper.mapDbEvents(database.eventsDao().getAllEvents()) }
                .doOnError {
                    it.printStackTrace()
                    Log.e(this::class.java.name, it.message)
                }
    }

    private fun saveIntoDB(events: List<Event>) {
        database.eventsDao().deleteAllEvents()
        events.map { database.eventsDao().insert(mapper.mapEvent(it)) }
    }
}