package com.example.mystoryapp

import com.example.mystoryapp.data.remote.response.Story

object DummyData {

    fun generateDummyStory(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/new-ui-logo.png",
                "dicoding",
                "$i",
                0.0,
                0.0
            )
            items.add(story)
        }
        return items
    }
}