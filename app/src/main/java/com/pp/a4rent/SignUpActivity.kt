package com.pp.a4rent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pp.a4rent.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val role = binding.radioGroup.checkedRadioButtonId.toString()

            // Check if any field is empty
            if (username.isEmpty() || password.isEmpty()) {
                // Show error message if any field is empty
                binding.tvSignUpStatus.text = "Please fill in all fields"
            } else if (isUsernameAlreadyRegistered(username)) {
                // Show error message if the username is already registered
                binding.tvSignUpStatus.text = "Username already registered"
            } else {
                // Save user information to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putString("role", role)
                editor.apply()

                // Add the registered username to the set
                addToRegisteredUsernames(username)

                // Registration successful, update status message and navigate to the login screen
                binding.tvSignUpStatus.text = "Registration successful"
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Navigate to MainActivity
        binding.btnLogo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun isUsernameAlreadyRegistered(username: String): Boolean {
        // Retrieve the list of registered usernames from SharedPreferences
        val registeredUsernames = sharedPreferences.getStringSet("registered_usernames", mutableSetOf()) ?: mutableSetOf()

        // Check if the provided username is already in the list
        return registeredUsernames.contains(username)
    }

    private fun addToRegisteredUsernames(username: String) {
        // Retrieve the current set of registered usernames then add the new username to the set
        val registeredUsernames = sharedPreferences.getStringSet("registered_usernames", mutableSetOf()) ?: mutableSetOf()
        registeredUsernames.add(username)

        // Save the updated set back to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putStringSet("registered_usernames", registeredUsernames)
        editor.apply()
    }
}
