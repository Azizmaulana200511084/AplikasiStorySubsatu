package com.aziz.aplikasistorysubsatu.ui.main.createstory

import androidx.lifecycle.ViewModel
import com.aziz.aplikasistorysubsatu.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun postStory(file: MultipartBody.Part, description: RequestBody) = storyRepository.postStory(file, description)
}