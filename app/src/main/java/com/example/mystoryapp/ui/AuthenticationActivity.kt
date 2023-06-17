package com.example.mystoryapp.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.remote.paging.StoryRepository
import com.example.mystoryapp.viewmodel.AuthenticationViewModel
import com.example.mystoryapp.viewmodel.UserPreferences
import com.example.mystoryapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "authentication")
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationViewModel: AuthenticationViewModel

    companion object {
        private val TAG = "AuthenticationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        Log.i(TAG, "Object datastore di auth activity: ${dataStore}")

        val pref = UserPreferences.getInstance(dataStore)
        val repo = StoryRepository(pref)
        authenticationViewModel = ViewModelProvider(this, ViewModelFactory(pref, repo)).get(
            AuthenticationViewModel::class.java
        )
        Log.i(TAG, "Object view model di auth activity: ${authenticationViewModel}")

        val mFragmentManager = supportFragmentManager
        val mLoginFragment = LoginFragment()
        val fragment = mFragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        if (fragment == null) {
            mFragmentManager
                .beginTransaction()
                .add(R.id.authentication_frame, mLoginFragment, LoginFragment::class.java.simpleName)
                .commit()
        }

    }
}