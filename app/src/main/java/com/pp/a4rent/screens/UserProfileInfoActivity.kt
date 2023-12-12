package com.pp.a4rent.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityUserProfileInfoBinding
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.UserRepository

class UserProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileInfoBinding
    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tenant_profile_info)

        this.binding = ActivityUserProfileInfoBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.tenantMenuToolbar)
        // Change the title
        supportActionBar?.title = "4Rent"

        // For changing the color of overflow icon
        // Get the drawable for the overflow icon
        val drawable = this.binding.tenantMenuToolbar.overflowIcon

        // Apply a tint to change the color of the overflow icon
        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.white))
            binding.tenantMenuToolbar.overflowIcon = wrappedDrawable
        }

        sharedPrefs = this.getSharedPreferences(packageName, Context.MODE_PRIVATE)
        this.userRepository = UserRepository(applicationContext)

        if (sharedPrefs.contains("USER_EMAIL")){
            userRepository.getUser(sharedPrefs.getString("USER_EMAIL", "NA").toString())
        }

        this.binding.btnUpdateProfile.setOnClickListener {
            userRepository.updateUserProfile(User(
                firstName = this.binding.etFirstName.text.toString(),
                lastName = this.binding.etLastName.text.toString(),
                email = this.binding.etEmail.text.toString(),
                password = this.binding.etPassword.text.toString(),
                phoneNumber = this.binding.etPhoneNumber.text.toString(),
                role = this.binding.tvRoleData.text.toString()
            ))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        userRepository.currentUser.observe(this) { user ->
            if (user != null) {
                this.binding.tvRoleData.setText(user.role)
                this.binding.etFirstName.setText(user.firstName)
                this.binding.etLastName.setText(user.lastName)
                this.binding.etEmail.setText(user.email)
                this.binding.etPassword.setText(user.password)
                this.binding.etPhoneNumber.setText(user.phoneNumber)
            }
        }
    }
}