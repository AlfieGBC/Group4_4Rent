package com.pp.a4rent.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityMyListingDetailsBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.User

class MyListingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingDetailsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var myListingsList = listOf<Property>()
    private var position: Int? = null
    private var userObj: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initiate shared preference
        sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if (intent != null){

            // get the user object
            userObj = if (intent.hasExtra("extra_userObj")) {
                intent.getSerializableExtra("extra_userObj") as User
            } else {null}

            // get listing position
            position = if (intent.hasExtra("extra_position")) {
                intent.getIntExtra("extra_position", -1)
            } else {null}

            if (userObj != null && position != null){
                // if user object exist

                // get myListings list from sharedPreference
                val myListingsListJson = sharedPreferences.getString(userObj!!.userId, "")
                if (myListingsListJson == ""){}else{
                    val gson = Gson()
                    val typeToken = object : TypeToken<List<Property>>() {}.type
                    myListingsList = gson.fromJson<List<Property>>(myListingsListJson, typeToken).toList()

                    // get current listing object from myListings list
                    val currListingObj = myListingsList[position!!]

                    this.binding.apply {
//                        propertyTypeSpinner.selectedItem = currListingObj.propertyType
                        etNumOfBedroomListingDetails.setText(currListingObj.numberOfBedroom.toString())
                        etNumOfKitchenListingDetails.setText(currListingObj.numberOKitchen.toString())
                        etNumOfBathroomListingDetails.setText(currListingObj.numberOfBathroom.toString())
                        etAreaListingDetails.setText(currListingObj.area.toString())
                        etDescriptionListingDetails.setText(currListingObj.description)
                        etAddressListingDetails.setText(currListingObj.propertyAddress)
                        etRentListingDetails.setText(currListingObj.rent.toString())
                        isAvailableListingDetails.isChecked = currListingObj.available
                    }
                }
            }
        }
        // get the listings list from shared preference

        // when Delete btn is clicked
//        binding.btnDeleteListingDetails.setOnClickListener {
//
//        }
    }
}