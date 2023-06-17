package com.example.mystoryapp.data.remote.paging

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.data.remote.retrofit.ApiService
import com.example.mystoryapp.viewmodel.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryPagingSource(private val apiService: ApiService, private val pref: UserPreferences) : PagingSource<Int, Story>() {
    companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryPagingSource"
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            Log.i(TAG, "in load func")
            val token = "Bearer ${getToken()}"
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStory2(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )

        } catch (exception: Exception) {
            Log.i(TAG, "$exception")
            return LoadResult.Error(exception)
        }
    }

    private fun getToken() : String {
        val token = runBlocking { pref.getToken().first() }
        return token
    }
}