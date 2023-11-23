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
import com.pp.a4rent.screens.LoginActivity
import com.pp.a4rent.screens.MyListingDetailsActivity
import java.io.Serializable

class MyListingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var myListingsList = mutableListOf<Property>()
    private var userObj: User? = null
    private lateinit var adapter: MyListingsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up menu
        setSupportActionBar(this.binding.menu)

        // initiate shared preference
        sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // get user object from intent
        val currIntent = this@MyListingsActivity.intent
        if (currIntent != null){
            userObj = if (currIntent.hasExtra("extra_userObj")) {
                currIntent.getSerializableExtra("extra_userObj") as User
            } else {null}
            if (userObj != null){
                // if user object exist

                // get myListings list from sharedPreference
                val myListingsListJson = sharedPreferences.getString(userObj!!.userId, "")
                if (myListingsListJson != ""){
                    val gson = Gson()
                    val typeToken = object : TypeToken<List<Property>>() {}.type
                    myListingsList = gson.fromJson<List<Property>>(myListingsListJson, typeToken).toMutableList()
                }

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

            } else {
                // if no user object passed through, redirect user to Login page
                val intent = Intent(this@MyListingsActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun listingRowClicked(position:Int){

        // pass through user object
        val intent = Intent(this@MyListingsActivity, MyListingDetailsActivity::class.java)
        intent.putExtra("extra_userObj", userObj)
        intent.putExtra("extra_position", position)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.landlord_profile_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_home_page -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.mi_post_rental -> {
                val intent = Intent(this, RentalFormActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.mi_my_account -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.mi_my_listings -> {
                val intent = Intent(this, MyListingsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume:MyListingsActivity", "onResume: user returned to MyListingsActivity.kt")

        // get the updated myListings list from sharePreference
        val updatedListJson = sharedPreferences.getString(userObj!!.userId, "")
        if (updatedListJson != ""){
            val gson = Gson()
            val typeToken = object : TypeToken<List<Property>>() {}.type
            val updatedList = gson.fromJson<List<Property>>(updatedListJson, typeToken).toMutableList()

            // clear the the original myListings list, and add the updated list
            myListingsList.clear()
            myListingsList.addAll(updatedList)
            adapter.notifyDataSetChanged()
        }

    }
}