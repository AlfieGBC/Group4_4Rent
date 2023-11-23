package com.pp.a4rent.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pp.a4rent.MainActivity
import com.pp.a4rent.databinding.ActivityRegisterBinding
import com.pp.a4rent.models.User
import java.util.UUID


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.registerButton.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val role = binding.role.findViewById<RadioButton>(binding.role.checkedRadioButtonId).text.toString()

            val minLength = 8
            val maxLength = 20

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (isEmailRegistered(email)) {
                Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
            } else if (!email.contains("@")) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            } else if (password.length < minLength || password.length > maxLength) {
                    Toast.makeText(this, "Password must be between $minLength and $maxLength characters long", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(UUID.randomUUID().toString(), firstName, lastName, email, password, "", role.toString())
                val gson = Gson()
                val userJson = gson.toJson(user)
                editor.putString(email, userJson)
                editor.apply()

                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.loginLink.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        binding.returnBack.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
    }

    private fun isEmailRegistered(email: String): Boolean {
        return sharedPreferences.contains(email)
    }
}