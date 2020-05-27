package com.youloveevents.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposables

class RxBroadcastReceiver(private val context: Context, private val intentFilter: IntentFilter) : ObservableOnSubscribe<Boolean> {

    companion object {
        fun create(context: Context): Observable<Boolean> {
            return Observable.create(RxBroadcastReceiver(context,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)))
        }
    }

    override fun subscribe(emitter: ObservableEmitter<Boolean>) {

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c1: Context, i1: Intent) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                emitter.onNext(activeNetwork?.isConnectedOrConnecting == true)
            }
        }

        context.registerReceiver(receiver, intentFilter)

        emitter.setDisposable(Disposables.fromRunnable {
            context.unregisterReceiver(receiver)
        })
    }
}