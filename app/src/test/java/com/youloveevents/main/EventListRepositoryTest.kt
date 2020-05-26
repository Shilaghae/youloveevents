package com.youloveevents.main

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
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
    private val apiEmbedded: ApiEmbedded = mock()
    private val apiPage: ApiPage = mock()
    private val apiEvent: ApiEvents = mock()
    private val apiEventImage: ApiEventImage = mock()
    private val apiEventPromoter: ApiEventPromoter = mock()
    private val apiEventDate: ApiEventDate = mock()
    private val apiEventStartDate: ApiEventStartDate = mock()
    private val apiEventSalePublic: ApiEventSalePublic = mock()
    private val apiEventSale: ApiEventSale = mock()
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
        whenever(apiDiscovery.embedded).thenReturn(apiEmbedded)
        whenever(apiDiscovery.page).thenReturn(apiPage)
        whenever(apiDiscovery.page).thenReturn(apiPage)
        whenever(apiEvent.name).thenReturn("Anna")
        whenever(apiEvent.id).thenReturn("1")
        whenever(apiEvent.url).thenReturn("http://anyurl")
        val images: List<ApiEventImage> = listOf(apiEventImage)
        whenever(apiEventImage.ratio).thenReturn("16_9")
        whenever(apiEventImage.height).thenReturn(200)
        whenever(apiEventImage.width).thenReturn(200)
        whenever(apiEventImage.url).thenReturn("http://anyurl")
        whenever(apiEvent.images).thenReturn(images)
        whenever(apiEvent.dates).thenReturn(null)
        whenever(apiEvent.promoter).thenReturn(apiEventPromoter)
        whenever(apiEvent.dates).thenReturn(apiEventDate)
        whenever(apiEventDate.start).thenReturn(apiEventStartDate)
        whenever(apiEventDate.timezone).thenReturn("Europe/London")
        whenever(apiEventStartDate.dateTime).thenReturn("2019-11-22T10:00:00Z")
        val events = listOf(apiEvent)
        whenever(apiEmbedded.events).thenReturn(events)
        whenever(apiEvent.sales).thenReturn(apiEventSalePublic)
        whenever(apiEventSalePublic.public).thenReturn(apiEventSale)
        whenever(apiEventSale.startDateTime).thenReturn("2019-11-22T10:00:00Z")
        whenever(apiEventSale.endDateTime).thenReturn("2019-11-24T10:00:00Z")
    }

    @Test
    fun shouldGetEventsAndMapThem() {
        whenever(api.events()).thenReturn(Single.just(apiDiscovery))

        underTest.getEvents().subscribe(testObserver)

        verify(mapper).mapApi(apiDiscovery)
        verify(mapper).mapDbEvents(listOf(eventDb))
        verify(mapper).mapEvent(event)
        verify(eventDao).deleteAllEvents()
        verify(eventDao).insert(eventDb)
        verify(eventDao).getAllEvents()
        verify(api).events()
        verifyNoMoreInteractions(api, eventDao, mapper)
    }

    @Test
    fun shouldReturnEventFromDbIfAnErrorOccur() {
        whenever(api.events()).thenReturn(Single.error(Throwable("An error")))

        underTest.getEvents().subscribe(testObserver)

        verify(mapper).mapDbEvents(listOf(eventDb))
        verify(eventDao).getAllEvents()
        verify(api).events()
        verifyNoMoreInteractions(api, eventDao, mapper)
    }
}