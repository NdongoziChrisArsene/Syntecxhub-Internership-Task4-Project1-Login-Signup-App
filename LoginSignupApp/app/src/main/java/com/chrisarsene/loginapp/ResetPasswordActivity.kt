package com.chrisarsene.loginapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var etEmail: EditText
    private lateinit var btnSend: Button
    private lateinit var tvError: TextView
    private lateinit var tvSuccess: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val repo = AuthRepository()
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        etEmail = findViewById(R.id.etEmail)
        btnSend = findViewById(R.id.btnSend)
        tvError = findViewById(R.id.tvError)
        tvSuccess = findViewById(R.id.tvSuccess)
        progressBar = findViewById(R.id.progressBar)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        btnSend.setOnClickListener {
            viewModel.resetState()
            viewModel.sendPasswordReset(etEmail.text.toString())
        }

        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                progressBar.isVisible = state is AuthState.Loading
                btnSend.isEnabled = state !is AuthState.Loading
                tvError.isVisible = state is AuthState.Error
                tvSuccess.isVisible = state is AuthState.ResetEmailSent

                when (state) {
                    is AuthState.Error -> tvError.text = state.message
                    is AuthState.ResetEmailSent -> {
                        tvSuccess.text = "Reset email sent. Check your inbox."
                        btnSend.isEnabled = false
                    }
                    else -> Unit
                }
            }
        }
    }
}
