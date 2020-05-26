package com.youloveevents.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroid : EventsDBEntity)

    @Query("DELETE FROM events_table")
    fun deleteAllEvents()

    @Query("SELECT * FROM events_table")
    fun getAllEvents() : List<EventsDBEntity>

//    @Query("SELECT * FROM events_table WHERE closeApproachDate >= :start AND closeApproachDate <= :end ORDER BY closeApproachDate ASC")
//    fun getInBetweenDate(start: Long, end: Long) : List<EventsDBEntity>
}