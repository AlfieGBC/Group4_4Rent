package com.pp.a4rent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.adapters.MyListingsAdapter
import com.pp.a4rent.databinding.ActivityMyListingsBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.PropertyRental
import com.pp.a4rent.models.User
import com.pp.a4rent.screens.LoginActivity
import java.io.Serializable

class MyListingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var myListingsList = listOf<Property>()
    private lateinit var adapter: MyListingsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up menu
        setSupportActionBar(this.binding.menu)

        // get user object from intent
        val currIntent = this@MyListingsActivity.intent
        if (currIntent != null){
            val userObj = if (currIntent.hasExtra("extra_userObj")) {
                currIntent.getSerializableExtra("extra_userObj") as User
            } else {null}
            if (userObj != null){
                // if user object exist

                // initiate shared preference
                sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
                editor = sharedPreferences.edit()

                // get myListings list from sharedPreference
                val myListingsListJson = sharedPreferences.getString(userObj.userId, "")
                if (myListingsListJson == ""){}else{
                    val gson = Gson()
                    val typeToken = object : TypeToken<List<Property>>() {}.type
                    myListingsList = gson.fromJson<List<Property>>(myListingsListJson, typeToken).toList()
                }

                // set up the adapter
                this.adapter = MyListingsAdapter(myListingsList)
                binding.rvMyListings.adapter = adapter
                binding.rvMyListings.layoutManager = LinearLayoutManager(this)
                binding.rvMyListings.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        LinearLayoutManager.VERTICAL
                    )
                )

                Log.d("myListingsList", "onCreate: myListingsList: $myListingsList\n" +
                        "myListingsList size: ${myListingsList.size}")

            } else {
                // if no user object passed through, redirect user to Login page
                val intent = Intent(this@MyListingsActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)


        // checks user is logged in or not
        val userJson = intent.getStringExtra("user")
        if (userJson != null) {
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)

            if (user.role == "Tenant") {
                menuInflater.inflate(R.menu.tenant_profile_options, menu)
            } else if (user.role == "Landlord") {
                menuInflater.inflate(R.menu.landlord_profile_options, menu)
            }

        } else {
            menuInflater.inflate(R.menu.guest_menu_options, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
//                val sidebarIntent = Intent(this@MainActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_post_rental -> {
                val intent = Intent(this, RentalFormActivity::class.java)
                // get the user info from login page
                val userJson = intent.getStringExtra("user")
                // pass this info to next page, which is tenant profile info page
                intent.putExtra("user", userJson)
                startActivity(intent)
                return true
            }

            R.id.mi_my_account -> {
                val intent = Intent(this, ProfileActivity::class.java)
                // get the user info from login page
                val userJson = intent.getStringExtra("user")
                // pass this info to next page, which is tenant profile info page
                intent.putExtra("user", userJson)
                startActivity(intent)
                return true
            }

            R.id.mi_my_listings -> {
                val intent = Intent(this, MyListingsActivity::class.java)
                // get the user info from login page
                val userJson = intent.getStringExtra("user")
                // pass this info to next page, which is tenant profile info page
                intent.putExtra("user", userJson)
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