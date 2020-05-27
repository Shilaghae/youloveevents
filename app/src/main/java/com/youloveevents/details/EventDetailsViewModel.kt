package com.youloveevents.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.youloveevents.base.BaseViewModel
import com.youloveevents.base.SchedulerProvider
import com.youloveevents.error.SimpleDomainException
import com.youloveevents.main.UiEvent
import com.youloveevents.util.Lce
import io.reactivex.Observable
import javax.inject.Inject

class EventDetailsViewModel @Inject constructor(
    private val interactor: EventDetailsInteractor,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val event = MutableLiveData<Lce<UiEvent>>()

    fun loadEventDetails(id: String) {
        disposeOnClear(Observable.defer { interactor.loadEventDetails(id) }
                .observeOn(schedulerProvider.io())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.mainThread())
                .subscribe({
                    event.value = it
                }, {
                    it.printStackTrace()
                    Log.d(this::class.java.name, it.message!!)
                    event.value = Lce.Error(throwable =
                    SimpleDomainException("Error while loading the events $it"))
                })
        )
    }
}