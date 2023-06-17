package com.example.mystoryapp.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences constructor(private val dataStore: DataStore<Preferences>) {

    private val USERNAME_KEY = stringPreferencesKey("username")
    private val PASSWORD_KEY = stringPreferencesKey("password")
    private val TOKEN_KEY = stringPreferencesKey("token")

    companion object {
        @Volatile
        private var INSTANCE: com.example.mystoryapp.viewmodel.UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): com.example.mystoryapp.viewmodel.UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
        }
    }
}