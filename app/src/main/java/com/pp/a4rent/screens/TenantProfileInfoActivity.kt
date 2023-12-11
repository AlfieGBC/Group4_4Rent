package com.pp.a4rent.screens

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.pp.a4rent.databinding.ActivityTenantProfileInfoBinding
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.UserRepository

class TenantProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTenantProfileInfoBinding
    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tenant_profile_info)

        this.binding = ActivityTenantProfileInfoBinding.inflate(layoutInflater)
        setContentView(this.binding.root)


//        val userJson = intent.getStringExtra("user")
//        if (userJson != null) {
//            val gson = Gson()
//            val user = gson.fromJson(userJson, User::class.java)
//
//            binding.firstName.text = user.firstName
//            binding.lastName.text = user.lastName
//            binding.email.text = user.email
//            binding.role.text = user.role
//        }


    }
}