package com.youloveevents.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.youloveevents.base.SchedulerProvider
import com.youloveevents.network.NetworkWatcher
import com.youloveevents.util.Lce
import com.youloveevents.util.StringSpecification
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.junit.MockitoJUnit

class EventListViewModelTest {

    @JvmField @Rule val mockitoRule = MockitoJUnit.rule()!!
    @JvmField @Rule val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var underTest: EventListViewModel

    private val eventListInteractor: EventListInteractor = mock()
    private val schedulerProvider: SchedulerProvider = mock()
    private var uiEventObserver: Observer<Lce<List<UiEvent>>> = mock()
    private var networkWatcher: NetworkWatcher = mock()

    @Before
    fun setup() {
        underTest = EventListViewModel(eventListInteractor, networkWatcher, schedulerProvider)
        whenever(schedulerProvider.mainThread()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        underTest.events.observeForever(uiEventObserver)
    }

    @Test
    fun shouldLoadEventsWhenThereIsInternet() {
        whenever(networkWatcher.onConnectionChanged()).thenReturn(Observable.just(true))
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
        val events = listOf(uiEvent)
        val lce = Lce.Data(events)
        whenever(eventListInteractor.loadEvent()).thenReturn(Observable.just(lce))

        underTest.loadEvents()

        verify(eventListInteractor).loadEvent()
        verify(schedulerProvider, times(1)).mainThread()
        verify(schedulerProvider, times(2)).io()
        argumentCaptor<Lce<List<UiEvent>>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            (firstValue as Lce.Data).apply {
                Truth.assertThat(this.data).isEqualTo(events)
            }
        }
    }

    @Test
    fun shouldLoadEventsWhenThereIsNoInternet() {
        whenever(networkWatcher.onConnectionChanged()).thenReturn(Observable.just(false))
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
        val events = listOf(uiEvent)
        val lce = Lce.Data(events)
        whenever(eventListInteractor.loadEvent()).thenReturn(Observable.just(lce))

        underTest.loadEvents()

        verify(eventListInteractor).loadEvent()
        verify(schedulerProvider, times(1)).mainThread()
        verify(schedulerProvider, times(2)).io()
        argumentCaptor<Lce<List<UiEvent>>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            (firstValue as Lce.Data).apply {
                Truth.assertThat(this.data).isEqualTo(events)
            }
        }
    }

    @Test
    fun shouldReturnAnError() {
        whenever(networkWatcher.onConnectionChanged()).thenReturn(Observable.just(false))
        whenever(eventListInteractor.loadEvent()).thenReturn(Observable.error(Throwable("An Error")))

        underTest.loadEvents()

        verify(schedulerProvider, times(1)).mainThread()
        verify(schedulerProvider, times(2)).io()
        argumentCaptor<Lce<List<UiEvent>>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            Truth.assertThat(firstValue).isInstanceOf(Lce.Error::class.java)
        }
    }
}