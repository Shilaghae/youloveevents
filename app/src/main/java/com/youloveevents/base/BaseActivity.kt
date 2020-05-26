package com.youloveevents.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {

    inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
        return ViewModelProvider(this, viewModelFactory)[T::class.java]
    }

    inline fun <reified T : ViewModel> AppCompatActivity.withViewModel(
        factory: ViewModelProvider.Factory,
        body: T.() -> Unit
    ): T {
        return getViewModel<T>(factory).also { it.body() }
    }

    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T) -> Unit) {
        return liveData.observe(this, Observer(body))
    }
}