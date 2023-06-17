package com.example.mystoryapp.data.local

import java.io.File

data class AddStoryRequest (
    val description: String,
    val photo: File
)