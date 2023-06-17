package com.example.mystoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystoryapp.data.remote.response.GetAllStoriesResponse
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel(private val pref: UserPreferences) : ViewModel() {

    companion object {
        const val TAG = "Map View Model"
    }

    private var _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getStoryLocations("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXpGbWgxSHZaNTducHBwR00iLCJpYXQiOjE2ODE4MTMwMDZ9.tyRW_szfIwlKIWXcvBjpbjZKA5AgjmwHbjk0JoNcmbo")
    }

    fun getStoryLocations(token: String) {
        Log.i(TAG, "in get")
        _isLoading.value = true
        val auth = "Bearer $token"
        val client = ApiConfig.getApiService().getAllStoryLocation(auth)
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>,
                response: Response<GetAllStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()!!.listStory
                    Log.i(TAG, _listStory.value!!.size.toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure2: ${t.message}")
            }
        })
    }

    fun getToken() : LiveData<String> {
        return pref.getToken().asLiveData()
    }
}