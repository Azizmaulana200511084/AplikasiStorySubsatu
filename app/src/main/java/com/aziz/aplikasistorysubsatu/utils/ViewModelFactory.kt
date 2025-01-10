package com.aziz.aplikasistorysubsatu.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aziz.aplikasistorysubsatu.di.Injection
import com.aziz.aplikasistorysubsatu.ui.auth.login.LoginViewModel
import com.aziz.aplikasistorysubsatu.ui.auth.signup.SignUpViewModel
import com.aziz.aplikasistorysubsatu.ui.main.createstory.CreateStoryViewModel
import com.aziz.aplikasistorysubsatu.ui.main.liststory.ListStoryViewModel
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Throws(IllegalArgumentException::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                ListStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> {
                CreateStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}