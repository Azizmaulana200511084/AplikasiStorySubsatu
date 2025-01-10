package com.aziz.aplikasistorysubsatu.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.aziz.aplikasistorysubsatu.data.StoryRepository

class SignUpViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun signUp(name: String, email: String, password: String) = storyRepository.postSignUp(name, email, password)
}