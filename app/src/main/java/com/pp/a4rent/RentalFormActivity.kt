package com.pp.a4rent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.databinding.ActivityRentalFormBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.PropertyType
import com.pp.a4rent.models.User
import com.pp.a4rent.screens.LoginActivity


class RentalFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRentalFormBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var propertiesList = mutableListOf<Property>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initiate shared preference
        sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // set up menu
        setSupportActionBar(this.binding.menu)

        // set up spinner
        val propertyTypeAdapter = ArrayAdapter(this, R.layout.spinner_item_property_types, PropertyType.displayNames)
        binding.propertyTypeSpinner.adapter = propertyTypeAdapter

        // when Publish button is clicked
        binding.btnPublish.setOnClickListener {

            // Get data from UI
            val email = binding.tvEmail.text.toString()
            val selectedPropertyTypeName = binding.propertyTypeSpinner.selectedItem.toString()
            val numOfBedrooms = binding.etNumOfBedroom.text.toString().toIntOrNull()?:0
            val numOfKitchens = binding.etNumOfKitchen.text.toString().toIntOrNull()?:0
            val numOfBathrooms = binding.etNumOfBathroom.text.toString().toIntOrNull()?:0
            val area = binding.etArea.text.toString().toDoubleOrNull()?:0.0
            val description = binding.etDescription.text.toString()
            val address = binding.etAddress.text.toString()
            val rent = binding.etRent.text.toString().toDoubleOrNull()?:0.0
            val isAvailable = binding.isAvailable.isChecked

            // Error handling
            if (
                numOfBedrooms <= 0 || numOfKitchens <= 0 || numOfBathrooms <= 0 || area <= 0.0 ||
                email.isEmpty() || description.isEmpty() || address.isEmpty() || rent <= 0.0
            ) {
                Snackbar.make(binding.root, "Please fill out all the fields.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // get user object from shared preference
            val userJson = sharedPreferences.getString(email, "")
            if (userJson == ""){
                // if user not logged in
                Snackbar.make(binding.root, "Please login first.", Snackbar.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return@setOnClickListener
            }
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)

            // get myListings list from sharedPreference
            val myListingsListJson = sharedPreferences.getString(user.userId, "")
            if (myListingsListJson != ""){
                val typeToken = object : TypeToken<List<Property>>() {}.type
                propertiesList = gson.fromJson<List<Property>>(myListingsListJson, typeToken).toMutableList()
            }

            // add property to the list
            val propertyType = PropertyType.fromDisplayName(selectedPropertyTypeName)
            val propertyToAdd = Property(
                propertyType, user, numOfBedrooms, numOfKitchens, numOfBathrooms, area,
                description, address, rent, isAvailable
            )
            propertiesList.add(propertyToAdd)
            Log.d("propertiesList", "onCreate: propertiesList: $propertiesList\n" +
                    "propertiesList size: ${propertiesList.size}")

            // convert list back to string, using user ID as KEY to store a list of listings
            val propertiesListJson = gson.toJson(propertiesList)
            editor.putString(user.userId, propertiesListJson)
            editor.apply()
            val intent = Intent(this, MyListingsActivity::class.java)
            intent.putExtra("extra_userObj", user)
            startActivity(intent)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.landlord_profile_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.mi_home_page -> {
                val intent = Intent(this@RentalFormActivity, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.mi_post_rental -> {
                val intent = Intent(this@RentalFormActivity, RentalFormActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.mi_my_account -> {
                val intent = Intent(this@RentalFormActivity, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.mi_my_listings -> {
                val intent = Intent(this@RentalFormActivity, MyListingsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun btnPublishClicked(){

    }
}