package com.example.plana22

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plana22.RoomDetail.DetailApp


/** The same repository that's needed for QuotesViewModel
is also passed to the factory*/
class OverviewViewModelFactory(val context: Context, private val detailApp: DetailApp)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OverviewViewModel(context, Application() ,detailApp) as T
    }

}