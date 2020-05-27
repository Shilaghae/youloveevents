package com.youloveevents.network

import android.app.Application
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkWatcher @Inject constructor(val application: Application) {

    var isConnectedObservable: Observable<Boolean>? = null

    fun onConnectionChanged(): Observable<Boolean> {
        if (isConnectedObservable == null) {
            isConnectedObservable = RxBroadcastReceiver.create(application)
                    .distinctUntilChanged()
                    .doOnDispose({ isConnectedObservable = null })
                    .share()
        }
        return isConnectedObservable!!
    }
}