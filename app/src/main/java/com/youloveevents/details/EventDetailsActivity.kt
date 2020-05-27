package com.youloveevents.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.youloveevents.R
import com.youloveevents.base.BaseActivity
import com.youloveevents.main.UiEvent
import com.youloveevents.main.UiEventImage
import com.youloveevents.util.Lce
import com.youloveevents.util.StringSpecification
import com.youloveevents.util.resolve
import kotlinx.android.synthetic.main.activity_event_details.*
import kotlinx.android.synthetic.main.activity_event_details_content.*
import javax.inject.Inject

class EventDetailsActivity : BaseActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: EventDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        if (intent.hasExtra(EVENT_DETAILS)) {
            val id = intent.getStringExtra(EVENT_DETAILS)

            viewModel = withViewModel(viewModelFactory) {
                observe(event, ::reload)
            }
            id?.let { viewModel.loadEventDetails(id) }
        }
    }

    private fun reload(uiModel: Lce<UiEvent>) {
        when (uiModel) {
            is Lce.Data -> {
                if (uiModel.data.bigImage is UiEventImage.Data) {
                    Picasso.get()
                            .load(uiModel.data.bigImage.uri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(activity_event_details_background_imageView)
                }
                activity_event_details_name.text = uiModel.data.name
                activity_event_details_promoter.also {
                    when (uiModel.data.promoter) {
                        is StringSpecification.Resource -> it.text = resolve(uiModel.data.promoter)
                        else -> it.isVisible = false
                    }
                }
                activity_event_details_promoter_description.also {
                    when (uiModel.data.promoterDescription) {
                        is StringSpecification.Resource -> it.text = resolve(uiModel.data.promoterDescription)
                        else -> it.isVisible = false
                    }
                }
                activity_event_details_sale.also {
                    when (uiModel.data.sale) {
                        is StringSpecification.Resource -> it.text = resolve(uiModel.data.sale)
                        else -> it.isVisible = false
                    }
                }
                activity_event_details_url.also {
                    if (uiModel.data.url != null) {
                        it.text = uiModel.data.url.toString()
                    } else it.isVisible = false
                }
            }
            else -> {
            }
        }
    }

    companion object {

        fun getIntent(context: Context, uiEvent: String): Intent {
            return Intent(context, EventDetailsActivity::class.java).apply {
                putExtra(EVENT_DETAILS, uiEvent)
            }
        }
    }
}

private const val EVENT_DETAILS = "EVENT_DETAILS"