package com.pp.a4rent.screens

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.google.gson.Gson
import com.pp.a4rent.databinding.ActivityAccountBinding
import com.pp.a4rent.models.User

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userJson = intent.getStringExtra("user")
        if (userJson != null) {
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)

            binding.firstName.text = user.firstName
            binding.lastName.text = user.lastName
            binding.email.text = user.email
            binding.role.text = user.role
        }
    }
}