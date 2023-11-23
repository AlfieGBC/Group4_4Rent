package com.pp.a4rent.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.gson.Gson
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

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (isEmailRegistered(email)) {
                    Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
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
        }
    }

    private fun isEmailRegistered(email: String): Boolean {
        return sharedPreferences.contains(email)
    }
}