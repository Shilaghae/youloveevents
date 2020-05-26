package com.youloveevents.base

import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class YouLoveEventsApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
    override fun applicationInjector(): AndroidInjector<YouLoveEventsApplication> {
        return DaggerAppComponent.factory()
                .create(this)
    }
}