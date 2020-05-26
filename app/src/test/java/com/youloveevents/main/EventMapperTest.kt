package com.youloveevents.main

import android.net.Uri
import com.youloveevents.network.ApiDiscovery
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.youloveevents.network.ApiEmbedded
import com.youloveevents.network.ApiEventDate
import com.youloveevents.network.ApiEventImage
import com.youloveevents.network.ApiEventPromoter
import com.youloveevents.network.ApiEventStartDate
import com.youloveevents.network.ApiEvents
import org.mockito.junit.MockitoJUnit
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.youloveevents.network.ApiPage
import com.youloveevents.util.UrlUtil

class EventMapperTest {
    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    private var urlUtil: UrlUtil = mock()
    private var uri: Uri = mock()

    private lateinit var underTest: EventsMapper

    @Before
    fun setup() {
        underTest = EventsMapper(urlUtil)
        whenever(urlUtil.toUri(any())).thenReturn(uri)
    }

    @Test
    fun shouldMapEvents() {

        val embedded = api_discovery {
            page {
                size = 1
            }
            embedded {
                event {
                    name = "Event Name 1"
                    id = "Event Id 1"

                    date {
                        start {
                            dateTime = "2019-11-22T10:00:00Z"
                        }
                        timezone = "Europe/London"
                    }

                    images {
                        ratio = "16_9"
                        height = 100
                        with = 100
                        url = "http://anyurl.co.uk"
                    }

                    images {
                        ratio = "3_2"
                        height = 300
                        with = 400
                        url = "http://anyurl.co.uk"
                    }

                    promoter {
                        id = "Promoter Event 1 id"
                        name = "Promoter Event 1 name"
                        description = "Promoter Event 1 description"
                    }
                }
            }
        }
        val result = underTest.mapApi(embedded)

        assertThat(result[0].name).isEqualTo("Event Name 1")
        assertThat(result[0].id).isEqualTo("Event Id 1")
        assertThat(result[0].date).isInstanceOf(EventDate.Data::class.java)
        assertThat((result[0].date as EventDate.Data).start).isEqualTo("2019-11-22T10:00:00Z")
        assertThat((result[0].date as EventDate.Data).timezone).isEqualTo("Europe/London")
        assertThat(result[0].promoter.name).isEqualTo("Promoter Event 1 name")
        assertThat(result[0].promoter.description).isEqualTo("Promoter Event 1 description")
        assertThat(result[0].smallImage).isInstanceOf(EventImage.Data::class.java)
        assertThat(result[0].bigImage).isInstanceOf(EventImage.Data::class.java)
    }

    @Test
    fun shouldNotMapEventsAsThereAreNone() {

        val embedded = api_discovery {
            page {
                size = 0
            }
        }
        val result = underTest.mapApi(embedded)

        assertThat(result).isEmpty()
    }

    @Test
    fun shouldMapWithNoImages() {

        val embedded = api_discovery {
            page {
                size = 1
            }
            embedded {
                event {
                    name = "Event Name 1"
                    id = "Event Id 1"

                    date {
                        start {
                            dateTime = "2019-11-22T10:00:00Z"
                        }
                        timezone = "Europe/London"
                    }

                    promoter {
                        id = "Promoter Event 1 id"
                        name = "Promoter Event 1 name"
                        description = "Promoter Event 1 description"
                    }
                }
            }
        }
        val result = underTest.mapApi(embedded)

        assertThat(result[0].name).isEqualTo("Event Name 1")
        assertThat(result[0].id).isEqualTo("Event Id 1")
        assertThat(result[0].date).isInstanceOf(EventDate.Data::class.java)
        assertThat((result[0].date as EventDate.Data).start).isEqualTo("2019-11-22T10:00:00Z")
        assertThat((result[0].date as EventDate.Data).timezone).isEqualTo("Europe/London")
        assertThat(result[0].promoter.name).isEqualTo("Promoter Event 1 name")
        assertThat(result[0].promoter.description).isEqualTo("Promoter Event 1 description")
        assertThat(result[0].smallImage).isInstanceOf(EventImage.None::class.java)
        assertThat(result[0].bigImage).isInstanceOf(EventImage.None::class.java)
    }

    @Test
    fun shouldMapWithNoDate() {

        val embedded = api_discovery {
            page {
                size = 1
            }
            embedded {
                event {
                    name = "Event Name 1"
                    id = "Event Id 1"

                    images {
                        ratio = "16_9"
                        height = 100
                        with = 100
                        url = "http://anyurl.co.uk"
                    }

                    images {
                        ratio = "3_2"
                        height = 300
                        with = 400
                        url = "http://anyurl.co.uk"
                    }

                    promoter {
                        id = "Promoter Event 1 id"
                        name = "Promoter Event 1 name"
                        description = "Promoter Event 1 description"
                    }
                }
            }
        }
        val result = underTest.mapApi(embedded)

        assertThat(result[0].name).isEqualTo("Event Name 1")
        assertThat(result[0].id).isEqualTo("Event Id 1")
        assertThat(result[0].date).isInstanceOf(EventDate.None::class.java)
        assertThat(result[0].promoter.name).isEqualTo("Promoter Event 1 name")
        assertThat(result[0].promoter.description).isEqualTo("Promoter Event 1 description")
        assertThat(result[0].smallImage).isInstanceOf(EventImage.Data::class.java)
        assertThat(result[0].bigImage).isInstanceOf(EventImage.Data::class.java)
    }

    @Test
    fun shouldMapFromEventToDBEvent() {
        val event = Event("You Rock",
                "1",
                EventImage.Data(uri),
                EventImage.Data(uri),
                EventPromoter("Anna", "A good one"),
                null,
                EventSale.Data("2019-11-22T10:00:00Z", "2019-11-24T10:00:00Z"),
                EventDate.Data("2019-11-22T10:00:00Z", "Europe/London")
        )

        whenever(uri.toString()).thenReturn("any uri")

        val result = underTest.mapEvent(event)

        assertThat(result.name).isEqualTo("You Rock")
        assertThat(result.id).isEqualTo("1")
        assertThat(result.date).isEqualTo("2019-11-22T10:00:00Z")
        assertThat(result.bigImage).isEqualTo("any uri")
        assertThat(result.smallImage).isEqualTo("any uri")
        assertThat(result.startToSale).isEqualTo("2019-11-22T10:00:00Z")
        assertThat(result.endToSale).isEqualTo("2019-11-24T10:00:00Z")
    }

    private fun api_discovery(block: ApiDiscoveryBuilder.() -> Unit): ApiDiscovery =
            ApiDiscoveryBuilder().apply(block).build()

    class ApiDiscoveryBuilder {
        var embedded: ApiEmbedded? = null
        var page: ApiPage? = null

        fun embedded(block: ApiEmbeddedBuilder.() -> Unit) {
            embedded = ApiEmbeddedBuilder().also(block).build()
        }

        fun page(block: ApiPageBuilder.() -> Unit) {
            page = ApiPageBuilder().also(block).build()
        }

        fun build(): ApiDiscovery {
            return mock<ApiDiscovery>().also {
                whenever(it.embedded).thenReturn(embedded)
                whenever(it.page).thenReturn(page)
            }
        }
    }

    class ApiPageBuilder {
        var size: Int = 0

        fun build(): ApiPage {
            return mock<ApiPage>().also {
                whenever(it.size).thenReturn(size)
            }
        }
    }

    class ApiEmbeddedBuilder {
        var events: MutableList<ApiEvents> = arrayListOf()

        fun event(block: ApiEventBuilder.() -> Unit) {
            events.add(ApiEventBuilder().apply(block).build())
        }

        fun build(): ApiEmbedded {
            return mock<ApiEmbedded>().also {
                whenever(it.events).thenReturn(events)
            }
        }
    }

    class ApiEventBuilder {
        var name: String? = null
        var id: String? = null
        var url: String? = null
        var date: ApiEventDate? = null
        var promoter: ApiEventPromoter? = null
        var images: MutableList<ApiEventImage> = mutableListOf()

        fun date(block: ApiEventDateBuilder.() -> Unit) {
            date = ApiEventDateBuilder().apply(block).build()
        }

        fun promoter(block: ApiEventPromoterBuilder.() -> Unit) {
            promoter = ApiEventPromoterBuilder().apply(block).build()
        }

        fun images(block: ApiEventImageBuilder.() -> Unit) {
            images.add(ApiEventImageBuilder().apply(block).build())
        }

        fun build(): ApiEvents {
            return mock<ApiEvents>().also {
                whenever(it.name).thenReturn(name)
                whenever(it.id).thenReturn(id)
                whenever(it.url).thenReturn(url)
                whenever(it.dates).thenReturn(date)
                whenever(it.promoter).thenReturn(promoter)
                whenever(it.images).thenReturn(images)
            }
        }
    }

    class ApiEventDateBuilder {

        var start: ApiEventStartDate? = null
        var timezone: String? = null

        fun start(block: ApiEventStartDateBuilder.() -> Unit) {
            start = ApiEventStartDateBuilder().apply(block).build()
        }

        fun build(): ApiEventDate {
            return mock<ApiEventDate>().also {
                whenever(it.start).thenReturn(start)
                whenever(it.timezone).thenReturn(timezone)
            }
        }
    }

    class ApiEventStartDateBuilder {
        var dateTime: String? = null

        fun build(): ApiEventStartDate {
            return mock<ApiEventStartDate>().also {
                whenever(it.dateTime).thenReturn(dateTime)
            }
        }
    }

    class ApiEventPromoterBuilder {
        var id: String? = null
        var name: String? = null
        var description: String? = null

        fun build(): ApiEventPromoter {
            return mock<ApiEventPromoter>().also {
                whenever(it.id).thenReturn(id)
                whenever(it.name).thenReturn(name)
                whenever(it.description).thenReturn(description)
            }
        }
    }

    class ApiEventImageBuilder {
        var ratio: String? = null
        var url: String? = null
        var with: Int = 0
        var height: Int = 0

        fun build(): ApiEventImage {
            return mock<ApiEventImage>().also {
                whenever(it.ratio).thenReturn(ratio)
                whenever(it.url).thenReturn(url)
                whenever(it.width).thenReturn(with)
                whenever(it.height).thenReturn(height)
            }
        }
    }
}