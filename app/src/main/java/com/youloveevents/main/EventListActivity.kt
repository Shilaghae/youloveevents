package com.youloveevents.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.youloveevents.R.layout
import com.youloveevents.base.BaseActivity
import com.youloveevents.util.Lce
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import javax.inject.Inject

class EventListActivity : BaseActivity() {

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
            eventAdapter = EventAdapter()
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
                val sb = StringBuilder()
                uiModel.data.forEach {
                    sb.append("${it.name} - ${it.url}  \n")
                }
                eventAdapter.setEvents(uiModel.data)
//                Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show()
            }
            is Lce.Error -> {
                loading(false)
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
                uiModel.throwable.printStackTrace()
                Log.d(this::class.java.name, uiModel.throwable.message)
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        activity_main_events_list_progressBar.isVisible = isLoading
        activity_main_events_list_recyclerView.isVisible = !isLoading
    }
}
