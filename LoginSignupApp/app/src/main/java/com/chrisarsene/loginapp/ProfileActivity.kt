package com.chrisarsene.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val repo = AuthRepository()
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        if (!viewModel.isLoggedIn) {
            goToLogin()
            return
        }

        val user = viewModel.currentUser!!

        findViewById<TextView>(R.id.tvName).text = user.displayName ?: "User"
        findViewById<TextView>(R.id.tvEmail).text = user.email ?: ""
        findViewById<TextView>(R.id.tvUid).text = "UID: ${user.uid}"
        findViewById<TextView>(R.id.tvVerified).text =
            if (user.isEmailVerified) "Email verified" else "Email not verified"

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log out") { _, _ ->
                    viewModel.logout()
                    goToLogin()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isLoggedIn) goToLogin()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        // prevent going back to login when already logged in
    }
}
