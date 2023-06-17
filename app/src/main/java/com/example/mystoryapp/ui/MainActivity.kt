package com.example.mystoryapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.remote.paging.LoadingStateAdapter
import com.example.mystoryapp.data.remote.paging.StoryRepository
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.viewmodel.ViewModelFactory
import com.example.mystoryapp.viewmodel.MainViewModel
import com.example.mystoryapp.viewmodel.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "authentication")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        Log.i(TAG, "Object datastore di main activity: ${dataStore}")

        val pref = UserPreferences.getInstance(dataStore)
        val repo = StoryRepository()
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref, repo)).get(
            MainViewModel::class.java
        )
        Log.i(TAG, "Object view model di main activity: ${mainViewModel}")

        var token = ""
        mainViewModel.getToken().observe(this) { res ->
            Log.i(TAG, "$res")
            if (res == null || res == "") {
                goToLogin()
            } else {
                token = res
                Log.i(TAG, "Token: ${token}")
                getData()
            }
        }
    }

    private fun getData() {
        val adapter = StoryAdapter()
        Log.i(TAG, adapter.toString())
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.listStory.observe(this) {
            adapter.submitData(lifecycle, it)
            Log.i(TAG, "after submit data")
            Log.i(TAG, it.toString())
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
            R.id.show_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        lifecycleScope.launch {
            mainViewModel.deleteToken()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}