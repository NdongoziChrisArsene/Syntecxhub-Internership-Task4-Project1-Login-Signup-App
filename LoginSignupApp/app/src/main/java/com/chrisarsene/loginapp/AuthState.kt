package com.chrisarsene.loginapp

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
    object ResetEmailSent : AuthState()
}
