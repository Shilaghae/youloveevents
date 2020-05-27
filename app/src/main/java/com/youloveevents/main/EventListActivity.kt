package com.youloveevents.main

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.youloveevents.R
import com.youloveevents.R.layout
import com.youloveevents.base.BaseActivity
import com.youloveevents.details.EventDetailsActivity
import com.youloveevents.main.EventAdapter.EventAction
import com.youloveevents.util.Lce
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class EventListActivity : BaseActivity(), EventAction {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: EventListViewModel

    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        viewModel = withViewModel(viewModelFactory) {
            observe(events, ::reload)
        }
        activity_main_events_list_recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            eventAdapter = EventAdapter(this@EventListActivity)
            adapter = eventAdapter
            setHasFixedSize(true)
        }
        viewModel.loadEvents()
    }

    private fun reload(uiModel: Lce<List<UiEvent>>) {
        when (uiModel) {
            is Lce.Loading -> {
                loading(true)
            }
            is Lce.Data -> {
                loading(false)
                showEventList(uiModel.data)
            }
            is Lce.Error -> {
                loading(false)
                Toast.makeText(this, R.string.error_check_your_connection, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        activity_main_events_list_progressBar.isVisible = isLoading
        activity_main_events_list_recyclerView.isVisible = !isLoading
        activity_main_events_emptyList_textView.isGone = true
    }

    private fun showEventList(events: List<UiEvent>) {
        eventAdapter.setEvents(events)
    }

    override fun showEmptyList(show: Boolean) {
        activity_main_events_emptyList_textView.isVisible = show
    }

    override fun onEventClicked(uiEvent: UiEvent) {
        startActivity(EventDetailsActivity.getIntent(this, uiEvent.id))
    }
}
