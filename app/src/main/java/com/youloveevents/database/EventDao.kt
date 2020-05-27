package com.youloveevents.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event : EventsDBEntity)

    @Query("DELETE FROM events_table")
    fun deleteAllEvents()

    @Query("SELECT * FROM events_table")
    fun getAllEvents() : List<EventsDBEntity>


    @Query("SELECT * FROM events_table WHERE id = :id")
    fun getEvent(id: String) : EventsDBEntity
}