package com.pp.a4rent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.databinding.ActivityProfileBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.User

import com.pp.a4rent.screens.BlogListActivity
import com.pp.a4rent.screens.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var myListingsList = mutableListOf<Property>()
    private var userObj: User? = null
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up menu
        setSupportActionBar(this.binding.menu)

        // initiate shared preference
        sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // get the user object from intent
//        if (intent != null) {
//            userObj = if (intent.hasExtra("extra_userObj")) {
//                intent.getSerializableExtra("extra_userObj") as User
//            } else {null}
//
//            // if userObj does exist, the uer is NOT logged in
//            if (userObj == null){
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            } else {
//
//                // prefill the profile page
//                binding.apply {
//                    etFirstName.setText(userObj!!.firstName)
//                    etLastName.setText(userObj!!.lastName)
//                    etEmail.setText(userObj!!.email)
//                    etPhoneNumber.setText(userObj!!.phoneNumber)
//                    etPassword.setText(userObj!!.password)
//                }
//
//                // when update button is clicked
//                binding.btnUpdateProfile.setOnClickListener {
//                    updateBtnClicked()
//                }
//            }
//
//        }

    }

    private fun updateBtnClicked(){
        // save all the fields to the current user object
        userObj!!.apply {
            firstName = binding.etFirstName.text.toString()
            lastName = binding.etLastName.text.toString()
            email = binding.etEmail.text.toString()
            phoneNumber = binding.etPhoneNumber.text.toString()
            password = binding.etPassword.text.toString()
        }

        val gson = Gson()
        val userJson = gson.toJson(userObj)
        editor.putString(userObj!!.email, userJson)
        editor.apply()

        // inform the user
        Snackbar.make(binding.root, "Update successfully!", Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)
        menuInflater.inflate(R.menu.landlord_profile_options, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_home -> {
                Log.d("TAG", "onOptionsItemSelected: Post Rental option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, MainActivity::class.java)

                // get the user info from login page
                val userJson = intent.getStringExtra("user")
                // pass this info to next page, which is tenant profile info page
                sidebarIntent.putExtra("user", userJson)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, BlogListActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_post_rental -> {
                // pass through the user object
                val intent = Intent(this, RentalFormActivity::class.java)
//                intent.putExtra("extra_userObj", userObj)
                startActivity(intent)
                return true
            }
            R.id.mi_my_account -> {

                // pass through the user object
                val intent = Intent(this, ProfileActivity::class.java)
//                intent.putExtra("extra_userObj", userObj)
                startActivity(intent)
                return true
            }
            R.id.mi_my_listings -> {

                // pass through the user object
                val intent = Intent(this, MyListingsActivity::class.java)
//                intent.putExtra("extra_userObj", userObj)
                startActivity(intent)
                return true
            }

            R.id.mi_logout -> {
                // navigate to 2nd screen
//                sharedPreferences.edit().clear().apply()
                val sidebarIntent = Intent(this, MainActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}