package com.example.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class AddStoryViewModel(private val pref: UserPreferences) : ViewModel() {

    companion object {
        private const val TAG = "Add Story View Model"
    }

    fun getToken() : LiveData<String> {
        return pref.getToken().asLiveData()
    }
}