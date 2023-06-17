package com.example.mystoryapp.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.remote.paging.StoryRepository
import com.example.mystoryapp.databinding.ActivityDetailBinding
import com.example.mystoryapp.viewmodel.DetailViewModel
import com.example.mystoryapp.viewmodel.UserPreferences
import com.example.mystoryapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "authentication")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    companion object {
        const val EXTRA_ID = "extra_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID)!!

        val pref = UserPreferences.getInstance(dataStore)
        val repo = StoryRepository()
        detailViewModel = ViewModelProvider(this, ViewModelFactory(pref, repo)).get(
            DetailViewModel::class.java
        )
        detailViewModel.getDetail(id)
        detailViewModel.story.observe(this, { obj ->
            setStory()
        })

        detailViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    private fun setStory() {
        val story = detailViewModel.story.value!!
        val url = story.photoUrl
        Glide.with(this@DetailActivity)
            .load(url)
            .into(binding.ivPhoto)
        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}