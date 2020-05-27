package com.youloveevents.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.youloveevents.base.BaseViewModel
import com.youloveevents.base.SchedulerProvider
import com.youloveevents.error.SimpleDomainException
import com.youloveevents.network.NetworkWatcher
import com.youloveevents.util.Lce
import javax.inject.Inject

class EventListViewModel @Inject constructor(
    private val interactor: EventListInteractor,
    private val networkWatcher: NetworkWatcher,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val events = MutableLiveData<Lce<List<UiEvent>>>()

    fun loadEvents() {

        disposeOnClear(networkWatcher.onConnectionChanged()
                .observeOn(schedulerProvider.io())
                .flatMap {
                    interactor.loadEvent()
                }
                .doOnError {
                    events.value = Lce.Error(throwable =
                    SimpleDomainException("Error while loading the events $it"))
                }
                .observeOn(schedulerProvider.mainThread())
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    events.value = it
                }, {
                    it.printStackTrace()
                    Log.d(this::class.java.name, it.message!!)
                    events.value = Lce.Error(throwable =
                    SimpleDomainException("Error while loading the events $it"))
                })
        )
    }
}