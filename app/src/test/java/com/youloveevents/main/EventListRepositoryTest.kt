package com.youloveevents.main

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.youloveevents.database.EventDao
import com.youloveevents.database.EventsDBEntity
import com.youloveevents.database.EventsDatabase
import com.youloveevents.network.ApiDiscovery
import com.youloveevents.network.ApiEmbedded
import com.youloveevents.network.ApiEventDate
import com.youloveevents.network.ApiEventImage
import com.youloveevents.network.ApiEventPromoter
import com.youloveevents.network.ApiEventSale
import com.youloveevents.network.ApiEventSalePublic
import com.youloveevents.network.ApiEventStartDate
import com.youloveevents.network.ApiEvents
import com.youloveevents.network.ApiInterface
import com.youloveevents.network.ApiPage
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class EventListRepositoryTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    private lateinit var underTest: EventListRepository
    private val mapper: EventsMapper = mock()
    private val api: ApiInterface = mock()
    private val database: EventsDatabase = mock()
    private val apiDiscovery: ApiDiscovery = mock()
    private var testObserver = TestObserver<List<Event>>()
    private var eventDao: EventDao = mock()
    private var event: Event = mock()
    private var eventDb: EventsDBEntity = mock()

    @Before
    fun setup() {
        underTest = EventListRepository(api, mapper, database)
        whenever(mapper.mapApi(any())).thenReturn(listOf(event))
        whenever(mapper.mapDbEvents(any())).thenReturn(listOf(event))
        whenever(mapper.mapEvent(any())).thenReturn(eventDb)
        whenever(eventDao.getAllEvents()).thenReturn(listOf(eventDb))
        whenever(database.eventsDao()).thenReturn(eventDao)
    }

    @Test
    fun shouldGetEventsAndMapThem() {
        whenever(api.events()).thenReturn(Single.just(apiDiscovery))

        underTest.loadEvents().subscribe(testObserver)

        verify(mapper).mapApi(apiDiscovery)
        verify(mapper, times(2)).mapDbEvents(listOf(eventDb))
        verify(mapper).mapEvent(event)
        verify(eventDao).deleteAllEvents()
        verify(eventDao).insert(eventDb)
        verify(eventDao, times(2)).getAllEvents()
        verify(api).events()
        verifyNoMoreInteractions(api, eventDao, mapper)
    }

    @Test
    fun shouldReturnEventFromDbIfAnErrorOccur() {
        whenever(api.events()).thenReturn(Single.error(Throwable("An error")))

        underTest.loadEvents().subscribe(testObserver)

        verify(mapper, times(2)).mapDbEvents(listOf(eventDb))
        verify(eventDao, times(2)).getAllEvents()
        verify(api).events()
        verifyNoMoreInteractions(api, eventDao, mapper)
    }
}