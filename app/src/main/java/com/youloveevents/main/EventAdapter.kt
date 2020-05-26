package com.youloveevents.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.youloveevents.R
import com.youloveevents.util.resolve
import kotlinx.android.synthetic.main.event_item.view.*

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<UiEvent> = mutableListOf()

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
        this.events = events
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setEvent(event: UiEvent) {
            itemView.event_item_textView_eventTitle.text = event.name
            itemView.event_item_date_textView.text = itemView.context.resolve(event.sale)
            if (event.smallImage is UiEventImage.Data) {
                Picasso.get()
                        .load(event.smallImage.uri)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(itemView.event_item_imageView)
            }
        }
    }
}