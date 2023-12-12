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
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.PropertyRepository
import com.pp.a4rent.screens.LoginActivity
import com.pp.a4rent.screens.MyListingDetailsActivity
import java.io.Serializable

class MyListingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingsBinding
//    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var editor: SharedPreferences.Editor
    private var myListingsList = mutableListOf<Property>()
    private lateinit var adapter: MyListingsAdapter
    private lateinit var propertyRepository: PropertyRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.propertyRepository = PropertyRepository(applicationContext)

        // set up menu
        setSupportActionBar(this.binding.menu)

        // initiate shared preference
//        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
//        editor = sharedPreferences.edit()


        // set up the adapter
        this.adapter = MyListingsAdapter(myListingsList) { pos -> listingRowClicked(pos) }
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

        // get user object from intent
//        val currIntent = this@MyListingsActivity.intent
//        if (currIntent != null){
//            userObj = if (currIntent.hasExtra("extra_userObj")) {
//                currIntent.getSerializableExtra("extra_userObj") as User
//            } else {null}
//            if (userObj != null){
//                // if user object exist
//
//                // get myListings list from sharedPreference
//                val myListingsListJson = sharedPreferences.getString(userObj!!.userId, "")
//                if (myListingsListJson != ""){
//                    val typeToken = object : TypeToken<List<Property>>() {}.type
//                    myListingsList = gson.fromJson<List<Property>>(myListingsListJson, typeToken).toMutableList()
//                }
//
//
//
//            } else {
//                // if no user object passed through, redirect user to Login page
//                val intent = Intent(this@MyListingsActivity, LoginActivity::class.java)
//                startActivity(intent)
//            }
//        }

    }


    private fun listingRowClicked(position:Int){
        val intent = Intent(this@MyListingsActivity, MyListingDetailsActivity::class.java)
        intent.putExtra("extra_property_id", myListingsList[position].propertyId)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)
        menuInflater.inflate(R.menu.landlord_profile_options, menu)

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
                val sidebarIntent = Intent(this, MainActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume:MyListingsActivity", "onResume: user returned to MyListingsActivity.kt")

        // get myListingsList from database
        propertyRepository.getAllPropertiesFromPropertyList()
        propertyRepository.allPropertiesInPropertyList
            .observe(this, androidx.lifecycle.Observer { propertyList ->
            if (propertyList != null){
                myListingsList.clear()
                myListingsList.addAll(propertyList)
                adapter.notifyDataSetChanged()
            }
        })
    }
}