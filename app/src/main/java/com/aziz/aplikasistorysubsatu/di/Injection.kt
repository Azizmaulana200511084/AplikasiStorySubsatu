package com.aziz.aplikasistorysubsatu.di

import android.content.Context
import com.aziz.aplikasistorysubsatu.data.StoryRepository
import com.aziz.aplikasistorysubsatu.data.remote.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository(apiService)
    }
}