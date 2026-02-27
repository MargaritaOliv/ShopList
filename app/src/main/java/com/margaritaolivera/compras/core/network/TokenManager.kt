package com.margaritaolivera.compras.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    }

    suspend fun saveSession(token: String, userId: String, userName: String, userEmail: String = "", userAvatar: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            if (userEmail.isNotEmpty()) preferences[USER_EMAIL_KEY] = userEmail
            if (userAvatar != null) preferences[USER_AVATAR_KEY] = userAvatar
        }
    }

    suspend fun getToken(): String? = context.dataStore.data.map { it[TOKEN_KEY] }.firstOrNull()
    suspend fun getUserId(): String? = context.dataStore.data.map { it[USER_ID_KEY] }.firstOrNull()
    suspend fun getUserEmail(): String? = context.dataStore.data.map { it[USER_EMAIL_KEY] }.firstOrNull()
    suspend fun getUserName(): String? = context.dataStore.data.map { it[USER_NAME_KEY] }.firstOrNull()
    suspend fun getUserAvatar(): String? = context.dataStore.data.map { it[USER_AVATAR_KEY] }.firstOrNull()
    fun getUserNameFlow(): Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }

    suspend fun clearSession() {
        context.dataStore.edit { preferences -> preferences.clear() }
    }
}