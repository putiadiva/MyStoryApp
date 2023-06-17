package com.example.mystoryapp.data.remote.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.data.remote.retrofit.ApiConfig

class StoryRepository {
    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(ApiConfig.getApiService()) }
        ).liveData
    }
}