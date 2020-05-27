package com.youloveevents.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.youloveevents.R
import com.youloveevents.util.resolve
import kotlinx.android.synthetic.main.event_item.view.*

class EventAdapter(private val listener: EventAction) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<UiEvent> = mutableListOf()

    interface EventAction {
        fun showEmptyList(show: Boolean)
        fun onEventClicked(uiEvent: UiEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.setEvent(event)
    }

    fun setEvents(events: List<UiEvent>) {
        if (events.isNullOrEmpty()) {
            listener.showEmptyList(true)
        } else {
            listener.showEmptyList(false)
            this.events = events
            notifyDataSetChanged()
        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setEvent(uiEvent: UiEvent) {
            itemView.event_item_textView_eventTitle.text = uiEvent.name
            itemView.event_item_date_textView.text = itemView.context.resolve(uiEvent.sale)
            itemView.setOnClickListener { listener.onEventClicked(uiEvent) }
            if (uiEvent.smallImage is UiEventImage.Data) {
                Picasso.get()
                        .load(uiEvent.smallImage.uri)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(itemView.event_item_imageView)
            }
        }
    }
}