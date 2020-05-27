package com.youloveevents.details

import com.youloveevents.EventConverter
import com.youloveevents.main.UiEvent
import com.youloveevents.util.Lce
import io.reactivex.Observable
import javax.inject.Inject

class EventDetailsInteractor @Inject constructor(
    private val repository: EventDetailsRepository,
    private val converter: EventConverter
) {

    fun loadEventDetails(id: String): Observable<Lce<UiEvent>> {
        return repository.loadEventDetails(id)
                .toObservable()
                .map { converter.convertEvent(it) }
                .startWith(Lce.Loading)
    }
}