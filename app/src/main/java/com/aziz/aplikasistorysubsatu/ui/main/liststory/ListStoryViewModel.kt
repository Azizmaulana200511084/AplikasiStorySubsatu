package com.aziz.aplikasistorysubsatu.ui.main.liststory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aziz.aplikasistorysubsatu.data.StoryRepository
import com.aziz.aplikasistorysubsatu.data.remote.response.stories.Story

class ListStoryViewModel(storyRepository: StoryRepository): ViewModel() {
    val stories: LiveData<PagingData<Story>> = storyRepository.getStories().cachedIn(viewModelScope)
}