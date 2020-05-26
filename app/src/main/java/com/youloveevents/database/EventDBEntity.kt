package com.youloveevents.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_table")
data class EventsDBEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val promoterName: String?,
    val promoterDescription: String?,
    val smallImage: String?,
    val bigImage: String?,
    val startToSale: String?,
    val endToSale: String?,
    val date: String?,
    val timezone: String?
)
