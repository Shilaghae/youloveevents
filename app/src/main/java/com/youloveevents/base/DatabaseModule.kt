package com.youloveevents.base

import android.app.Application
import com.youloveevents.database.EventsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideEventsDatabase(application: Application): EventsDatabase {
        return EventsDatabase.build(application)
    }
}