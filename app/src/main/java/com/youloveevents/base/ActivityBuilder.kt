package com.youloveevents.base

import com.youloveevents.main.EventListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity() : EventListActivity
}