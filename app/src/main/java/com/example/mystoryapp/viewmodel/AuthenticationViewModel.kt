package com.example.mystoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.mystoryapp.data.local.LoginRequest
import com.example.mystoryapp.data.local.RegisterRequest
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRegisterLoading = MutableLiveData<Boolean>()
    val isRegisterLoading: LiveData<Boolean> = _isRegisterLoading

    private val _isLoginSuccess = MutableLiveData<Boolean>()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private val _isRegisterSuccess = MutableLiveData<Boolean>()
    val isRegisterSuccess: LiveData<Boolean> = _isRegisterSuccess

    companion object {
        private const val TAG = "Auth View Model"
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun login(loginRequest: LoginRequest) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(loginRequest)
        client.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isLoginSuccess.value = true
                    // ambil token dan save di datastore.
                    Log.i(TAG, "berhasil login")
                    val token = response.body()!!.loginResult.token
                    saveToken(token)

                } else {
                    _isLoginSuccess.value = false
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun register(registerRequest: RegisterRequest) {
        _isRegisterLoading.value = true
        val client = ApiConfig.getApiService().register(registerRequest)
        client.enqueue(object : Callback<com.example.mystoryapp.data.remote.response.Response> {
            override fun onResponse(
                call: Call<com.example.mystoryapp.data.remote.response.Response>,
                response: retrofit2.Response<com.example.mystoryapp.data.remote.response.Response>
            ) {
                _isRegisterLoading.value = false
                if (response.isSuccessful) {
                    _isRegisterSuccess.value = true

                } else {
                    _isRegisterSuccess.value = false
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<com.example.mystoryapp.data.remote.response.Response>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}