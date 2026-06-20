package com.t.quickapply.data.remote.config

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

object RemoteConfigProvider {

    private val remoteConfig = Firebase.remoteConfig

    init {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        )
        remoteConfig.setDefaultsAsync(
            mapOf("groq_api_key" to "")
        )
    }

    suspend fun init() {
        try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
        }
    }

    fun getGroqApiKey(): String {
        return remoteConfig.getString("groq_api_key")
    }
}