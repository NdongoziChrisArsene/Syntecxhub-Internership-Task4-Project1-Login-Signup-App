package com.chrisarsene.loginapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    val isLoggedIn get() = repo.currentUser != null
    val currentUser get() = repo.currentUser

    fun login(email: String, password: String) {
        val error = validateLoginFields(email, password)
        if (error != null) {
            _state.value = AuthState.Error(error)
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repo.login(email.trim(), password)
            _state.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        val error = validateSignupFields(name, email, password, confirmPassword)
        if (error != null) {
            _state.value = AuthState.Error(error)
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repo.signup(name.trim(), email.trim(), password)
            _state.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Signup failed")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _state.value = AuthState.Error("Please enter your email address")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _state.value = AuthState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repo.sendPasswordReset(email.trim())
            _state.value = if (result.isSuccess) {
                AuthState.ResetEmailSent
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }

    fun logout() {
        repo.logout()
        _state.value = AuthState.Idle
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }

    private fun validateLoginFields(email: String, password: String): String? {
        if (email.isBlank()) return "Email is required"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "Enter a valid email"
        if (password.isBlank()) return "Password is required"
        if (password.length < 6) return "Password must be at least 6 characters"
        return null
    }

    private fun validateSignupFields(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (name.isBlank()) return "Full name is required"
        if (name.trim().length < 2) return "Name must be at least 2 characters"
        if (email.isBlank()) return "Email is required"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "Enter a valid email"
        if (password.isBlank()) return "Password is required"
        if (password.length < 6) return "Password must be at least 6 characters"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }
}
