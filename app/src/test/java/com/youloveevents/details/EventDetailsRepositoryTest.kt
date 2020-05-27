package com.youloveevents.details

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.youloveevents.database.EventDao
import com.youloveevents.database.EventsDBEntity
import com.youloveevents.database.EventsDatabase
import com.youloveevents.main.Event
import com.youloveevents.EventsMapper
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class EventDetailsRepositoryTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    private lateinit var underTest: EventDetailsRepository
    private var testObserver = TestObserver<Event>()
    private val database: EventsDatabase = mock()
    private val mapper: EventsMapper = mock()
    private val event : Event = mock()
    private val events : List<Event> = mock()
    private val eventDao : EventDao = mock()
    private val eventDbEntity : EventsDBEntity = mock()

    @Before
    fun setUp() {
        underTest = EventDetailsRepository(mapper, database)
        whenever(mapper.mapDbEvents(any())).thenReturn(events)
        whenever(database.eventsDao()).thenReturn(eventDao)
        whenever(eventDao.getEvent(any())).thenReturn(eventDbEntity)
    }

    @Test
    fun shouldLoadEventDetails() {
        val result = underTest.loadEventDetails("id1").subscribe(testObserver)

        verify(database).eventsDao()
        verify(eventDao).getEvent("id1")
        verify(mapper).mapDbEvent(eventDbEntity)

        verifyNoMoreInteractions(database, eventDao, mapper)
    }
}