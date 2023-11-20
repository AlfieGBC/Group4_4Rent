package com.pp.a4rent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")

            val enteredUsername = etUsername.text.toString()
            val enteredPassword = etPassword.text.toString()

            // Check if the username and password match
            if (enteredUsername == savedUsername && enteredPassword == savedPassword) {
                // Login successful, navigate to the main screen
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Login failed, show error message
                Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
