package com.example.mystoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.mystoryapp.data.remote.paging.StoryPagingSource
import com.example.mystoryapp.data.remote.paging.StoryRepository
import com.example.mystoryapp.data.remote.response.GetAllStoriesResponse
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreferences, private val repo: StoryRepository) : ViewModel() {

    companion object {
        const val TAG = "Main View Model"
    }

    val listStory: LiveData<PagingData<Story>> =
        repo.getStory().cachedIn(viewModelScope)

    fun getToken() : LiveData<String> {
        return pref.getToken().asLiveData()
    }

    suspend fun deleteToken() {
        return pref.deleteToken()
    }
}