package com.example.mystoryapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetAllStoriesResponse(

	@field:SerializedName("listStory")
	val listStory: List<Story>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class Story(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lon")
	val lon: Double,

	@field:SerializedName("lat")
	val lat: Double

) : Parcelable

//@Parcelize
//data class StoryLocation(
//
//	@field:SerializedName("photoUrl")
//	val photoUrl: String,
//
//	@field:SerializedName("name")
//	val name: String,
//
//	@field:SerializedName("lon")
//	val lon: Float,
//
//	@field:SerializedName("id")
//	val id: String,
//
//	@field:SerializedName("lat")
//	val lat: Float
//
//) : Parcelable
