package com.example.mystoryapp.data.remote.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.viewmodel.UserPreferences

class StoryRepository(private val pref: UserPreferences) {
    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(ApiConfig.getApiService(), pref) }
        ).liveData
    }


}