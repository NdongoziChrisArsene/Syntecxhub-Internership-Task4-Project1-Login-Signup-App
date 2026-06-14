package com.chrisarsene.loginapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvGoToSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val repo = AuthRepository()
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        if (viewModel.isLoggedIn) {
            goToProfile()
            return
        }

        bindViews()
        observeState()
        setupClicks()
    }

    private fun bindViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvGoToSignup = findViewById(R.id.tvGoToSignup)
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                progressBar.isVisible = state is AuthState.Loading
                btnLogin.isEnabled = state !is AuthState.Loading
                tvError.isVisible = state is AuthState.Error

                when (state) {
                    is AuthState.Success -> goToProfile()
                    is AuthState.Error -> tvError.text = state.message
                    else -> Unit
                }
            }
        }
    }

    private fun setupClicks() {
        btnLogin.setOnClickListener {
            viewModel.resetState()
            viewModel.login(
                etEmail.text.toString(),
                etPassword.text.toString()
            )
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun goToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}
