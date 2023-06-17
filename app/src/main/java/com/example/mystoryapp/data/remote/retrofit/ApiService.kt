package com.example.mystoryapp.data.remote.retrofit

import com.example.mystoryapp.data.local.LoginRequest
import com.example.mystoryapp.data.local.RegisterRequest
import com.example.mystoryapp.data.remote.response.DetailStoryResponse
import com.example.mystoryapp.data.remote.response.GetAllStoriesResponse
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/v1/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Response>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/v1/stories")
    @Multipart
    fun addNewStory(
        @Header ("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<Response>

    @GET("/v1/stories")
    suspend fun getAllStory2(
        @Header ("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesResponse

    @GET("/v1/stories/{id}")
    fun getDetailStory(@Header ("Authorization") token: String, @Path("id") id: String): Call<DetailStoryResponse>

    @GET("/v1/stories?location=1")
    fun getAllStoryLocation(@Header ("Authorization") token: String): Call<GetAllStoriesResponse>
}
