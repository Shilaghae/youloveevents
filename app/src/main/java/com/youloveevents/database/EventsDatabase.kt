package com.youloveevents.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventsDBEntity::class], version = 1)
abstract class EventsDatabase : RoomDatabase() {
    abstract fun eventsDao(): EventDao

    companion object Instance {

        private var instance: EventsDatabase? = null

        fun build(application: Application): EventsDatabase {
            if(instance == null) {
                synchronized(EventsDatabase::class){
                    if(instance == null){
                        instance = Room.databaseBuilder(application.applicationContext, EventsDatabase::class.java, "events_table").build()
                    }
                }
            }
            return instance!!
        }
    }
}