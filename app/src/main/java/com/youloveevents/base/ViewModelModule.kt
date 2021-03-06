package com.youloveevents.base

import androidx.lifecycle.ViewModel
import com.youloveevents.details.EventDetailsViewModel
import com.youloveevents.main.EventListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(EventListViewModel::class)
    internal abstract fun bindEventListViewModel(viewModel: EventListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EventDetailsViewModel::class)
    internal abstract fun bindEventDetailsViewModel(viewModel: EventDetailsViewModel): ViewModel
}