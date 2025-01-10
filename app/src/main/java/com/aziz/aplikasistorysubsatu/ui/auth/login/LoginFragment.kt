package com.aziz.aplikasistorysubsatu.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.aziz.aplikasistorysubsatu.R
import com.aziz.aplikasistorysubsatu.data.Result
import com.aziz.aplikasistorysubsatu.data.remote.response.login.LoginResponse
import com.aziz.aplikasistorysubsatu.databinding.FragmentLoginBinding
import com.aziz.aplikasistorysubsatu.utils.Preference
import com.aziz.aplikasistorysubsatu.utils.ViewModelFactory

private val String.code: Any
    get() {
        var result = 401
        for (char in this) {
            result += char.toInt()
        }
        return result
    }


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLoginDontHaveAccount.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.signUpFragment))

        binding.btLogin.setOnClickListener{
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            login(email, password)
        }

        val isFromSignUp: Boolean? = arguments?.getBoolean("is_from_sign_up")
        if (isFromSignUp != null && isFromSignUp) {
            onBackPressed()
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        if (password.length < 8) {
            return false
        }
        return true
    }


    private fun login(email: String, password: String) {
        if (!validateCredentials(email, password)) {
            Toast.makeText(requireContext(), "Email salah", Toast.LENGTH_LONG).show()
            return
        }

        loginViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    processLogin(result.data)
                    showLoading(false)
                }
                is Result.Error -> {
                    showLoading(false)
                    if (result.error.code == 401) {
                        Toast.makeText(requireContext(), "Email salah", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Email atau Password Salah", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun processLogin(data: LoginResponse) {
        if (data.error) {
            Toast.makeText(requireContext(), data.message, Toast.LENGTH_LONG).show()
        } else {
            Preference.saveToken(data.loginResult.token, requireContext())
            Toast.makeText(requireContext(), "Login berhasil, Selamat Datang...", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
            requireActivity().finish()
        }
    }


    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun showLoading(state: Boolean) {
        binding.pbLogin.isVisible = state
        binding.edLoginEmail.isInvisible = state
        binding.edLoginPassword.isInvisible = state
        binding.btLogin.isInvisible = state
        binding.textView6.isInvisible = state
        binding.tvLoginDontHaveAccount.isInvisible = state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}