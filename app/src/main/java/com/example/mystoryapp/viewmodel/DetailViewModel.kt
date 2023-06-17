package com.example.mystoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.mystoryapp.data.remote.response.DetailStoryResponse
import com.example.mystoryapp.data.remote.response.DetailedStory
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val pref: UserPreferences) : ViewModel() {

    companion object {
        const val TAG = "Detail View Model"
    }

    private var _story = MutableLiveData<DetailedStory>()
    val story: LiveData<DetailedStory> = _story

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetail(id: String) {
        _isLoading.value = true
        val token = getToken()
        Log.i(TAG, "Token: $token")
        val auth = "Bearer $token"
        val client = ApiConfig.getApiService().getDetailStory(auth, id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _isLoading.value= false
                if (response.isSuccessful) {
                    _story.value = response.body()!!.detailedStory
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure2: ${t.message}")
            }
        })
    }

    private fun getToken() : String {
        val token = runBlocking { pref.getToken().first() }
        return token
    }
}