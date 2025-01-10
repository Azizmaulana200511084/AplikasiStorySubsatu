package com.aziz.aplikasistorysubsatu.ui.auth.login

import androidx.lifecycle.ViewModel
import com.aziz.aplikasistorysubsatu.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) = storyRepository.postLogin(email, password)
}