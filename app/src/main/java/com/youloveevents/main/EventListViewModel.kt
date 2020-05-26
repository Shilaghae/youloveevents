package com.youloveevents.main

import androidx.lifecycle.MutableLiveData
import com.youloveevents.base.BaseViewModel
import com.youloveevents.base.SchedulerProvider
import com.youloveevents.util.Lce
import javax.inject.Inject

class EventListViewModel @Inject constructor(
    private val interactor: EventListInteractor,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val events = MutableLiveData<Lce<List<UiEvent>>>()

    fun loadEvents() {
        disposeOnClear(interactor.loadEvent()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.mainThread())
                .onErrorReturn { Lce.Error(it) }
                .subscribe {
                    events.value = it
                })
    }
}