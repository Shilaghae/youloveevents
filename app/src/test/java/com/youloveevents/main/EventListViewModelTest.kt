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

    @Before
    fun setup() {
        underTest = EventListViewModel(eventListInteractor, schedulerProvider)
        whenever(schedulerProvider.mainThread()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        underTest.events.observeForever(uiEventObserver)
    }

    @Test
    fun shouldLoadEvents() {
        val uiEvent = UiEvent("You Rock",
                UiEventImage.Void,
                UiEventImage.Void,
                mock(),
                "Europe/London",
                StringSpecification.Text("Any 1"),
                StringSpecification.Text("Any 2")
        )
        val events = listOf(uiEvent)
        val lce = Lce.Data(events)
        whenever(eventListInteractor.loadEvent()).thenReturn(Observable.just(lce))

        underTest.loadEvents()

        verify(eventListInteractor).loadEvent()
        verify(schedulerProvider, times(1)).mainThread()
        verify(schedulerProvider, times(1)).io()
        argumentCaptor<Lce<List<UiEvent>>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            (firstValue as Lce.Data).apply {
                Truth.assertThat(this.data).isEqualTo(events)
            }
        }
    }

    @Test
    fun shouldReturnAnError() {
        whenever(eventListInteractor.loadEvent()).thenReturn(Observable.error(Throwable("An Error")))

        underTest.loadEvents()

        verify(schedulerProvider, times(1)).mainThread()
        verify(schedulerProvider, times(1)).io()
        argumentCaptor<Lce<List<UiEvent>>>().apply {
            verify(uiEventObserver, times(1)).onChanged(capture())
            Truth.assertThat(firstValue).isInstanceOf(Lce.Error::class.java)
        }
    }
}