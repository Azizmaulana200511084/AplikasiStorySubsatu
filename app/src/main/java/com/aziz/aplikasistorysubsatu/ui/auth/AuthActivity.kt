package com.aziz.aplikasistorysubsatu.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aziz.aplikasistorysubsatu.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()
    }
}