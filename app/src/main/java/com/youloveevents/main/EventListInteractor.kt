package com.youloveevents.main

import com.youloveevents.EventConverter
import com.youloveevents.error.SimpleDomainException
import com.youloveevents.util.Lce
import io.reactivex.Observable
import javax.inject.Inject

class EventListInteractor @Inject constructor(
    private val repository: EventListRepository,
    private val converter: EventConverter
) {

    fun loadEvent(): Observable<Lce<List<UiEvent>>> {
        return repository.loadEvents()
                .map { converter.convertEvents(it) }
                .startWith(Lce.Loading)
    }
}