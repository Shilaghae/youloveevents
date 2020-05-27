package com.youloveevents.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.youloveevents.base.SchedulerProvider
import com.youloveevents.main.UiEvent
import com.youloveevents.main.UiEventImage
import com.youloveevents.util.Lce
import com.youloveevents.util.StringSpecification
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.junit.MockitoJUnit

class EventDetailsViewModelTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!
    @JvmField @Rule val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val interactor: EventDetailsInteractor = mock()
    private val schedulerProvider: SchedulerProvider = mock()
    private var uiEventObserver: Observer<Lce<UiEvent>> = mock()

    private lateinit var underTest: EventDetailsViewModel

    @Before
    fun setUp() {
        underTest = EventDetailsViewModel(interactor, schedulerProvider)
        whenever(schedulerProvider.mainThread()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        underTest.event.observeForever(uiEventObserver)
    }

    @Test
    fun shouldLoadTheEventDetails() {
        val uiEvent = UiEvent(
                "id1",
                "You Rock",
                UiEventImage.Void,
                UiEventImage.Void,
                mock(),
                "Europe/London",
                StringSpecification.Text("Any 1"),
                StringSpecification.Text("Any 2"),
                StringSpecification.Text("Any 3")
        )

        whenever(interactor.loadEventDetails(any())).thenReturn(Observable.just(Lce.Data(uiEvent)))

        underTest.loadEventDetails("id1")

        verify(schedulerProvider, times(2)).io()
        verify(schedulerProvider).mainThread()
        argumentCaptor<Lce<UiEvent>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            (firstValue as Lce.Data).apply {
                Truth.assertThat(this.data).isEqualTo(uiEvent)
            }
        }
        verifyNoMoreInteractions(schedulerProvider, uiEventObserver)
    }

    @Test
    fun shouldReturnAnError() {

        whenever(interactor.loadEventDetails(any())).thenReturn(Observable.just(Lce.Error(Throwable("An error"))))

        underTest.loadEventDetails("id1")

        verify(schedulerProvider, times(2)).io()
        verify(schedulerProvider).mainThread()
        argumentCaptor<Lce<UiEvent>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            (firstValue as Lce.Error)
        }
        verifyNoMoreInteractions(schedulerProvider, uiEventObserver)
    }
}