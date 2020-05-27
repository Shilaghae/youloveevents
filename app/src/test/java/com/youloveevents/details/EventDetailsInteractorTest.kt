package com.youloveevents.details

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.youloveevents.EventConverter
import com.youloveevents.main.Event
import com.youloveevents.main.UiEvent
import com.youloveevents.util.Lce
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit

class EventDetailsInteractorTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!

    private lateinit var underTest: EventDetailsInteractor
    private var testObserver = TestObserver<Lce<UiEvent>>()
    private val eventDetailsRepository: EventDetailsRepository = mock()
    private val eventConverter: EventConverter = mock()
    private val event : Event = mock()

    @Before
    fun setUp() {
        underTest = EventDetailsInteractor(eventDetailsRepository, eventConverter)
        whenever(eventDetailsRepository.loadEventDetails(any())).thenReturn(Single.just(event))
    }

    @Test
    fun shouldLoadEventDetails() {
        val result = underTest.loadEventDetails("id1").subscribe(testObserver)

        verify(eventDetailsRepository).loadEventDetails("id1")
        verify(eventConverter).convertEvent(event)

        verifyNoMoreInteractions(eventDetailsRepository, eventConverter)
    }
}