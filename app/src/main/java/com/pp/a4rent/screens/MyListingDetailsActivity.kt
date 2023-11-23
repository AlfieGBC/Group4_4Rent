package com.pp.a4rent.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityMyListingDetailsBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.PropertyType
import com.pp.a4rent.models.User

class MyListingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingDetailsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var myListingsList = mutableListOf<Property>()
    private var position: Int? = null
    private var userObj: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up spinner
        val propertyTypeAdapter = ArrayAdapter(this, R.layout.spinner_item_property_types, PropertyType.displayNames)
        binding.propertyTypeSpinnerListingDetails.adapter = propertyTypeAdapter

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
                    myListingsList =
                        gson.fromJson<List<Property>>(myListingsListJson, typeToken).toMutableList()

                    // get current listing object from myListings list
                    val currListingObj = myListingsList[position!!]

                    this.binding.apply {

                        // set spinner to previous state
                        val spinnerPosition = getSpinnerPositionForPropertyType(currListingObj.propertyType)
                        propertyTypeSpinnerListingDetails.setSelection(spinnerPosition)

                        // set the rest fields to previous states
                        etNumOfBedroomListingDetails.setText(currListingObj.numberOfBedroom.toString())
                        etNumOfKitchenListingDetails.setText(currListingObj.numberOKitchen.toString())
                        etNumOfBathroomListingDetails.setText(currListingObj.numberOfBathroom.toString())
                        etAreaListingDetails.setText(currListingObj.area.toString())
                        etDescriptionListingDetails.setText(currListingObj.description)
                        etAddressListingDetails.setText(currListingObj.propertyAddress)
                        etRentListingDetails.setText(currListingObj.rent.toString())
                        isAvailableListingDetails.isChecked = currListingObj.available
                    }

                    // when SAVE button clicked
                    binding.btnSaveListingDetails.setOnClickListener {
                        btnSaveClicked(currListingObj)
                        finish()
                    }

                    // when Delete btn is clicked
                    binding.btnDeleteListingDetails.setOnClickListener {
                        btnDeleteClicked()
                        finish()
                    }

                }
            }
        }

    }

    fun getSpinnerPositionForPropertyType(propertyType: PropertyType): Int {
        val displayNames = PropertyType.displayNames
        return displayNames.indexOf(propertyType.displayName)
    }

    fun btnDeleteClicked(){
        // remove current listing object from myListings List
        myListingsList.remove(myListingsList[position!!])

        // update sharedPreference
        val gson = Gson()
        val updatedListJson = gson.toJson(myListingsList)
        editor.putString(userObj!!.userId, updatedListJson)
        editor.apply()
    }

    fun btnSaveClicked(currListingObj: Property){
        // Get data from UI
        val selectedPropertyTypeName = binding.propertyTypeSpinnerListingDetails.selectedItem.toString()
        val numOfBedrooms = binding.etNumOfBedroomListingDetails.text.toString().toIntOrNull()?:0
        val numOfkitchens = binding.etNumOfKitchenListingDetails.text.toString().toIntOrNull()?:0
        val numOfbathrooms = binding.etNumOfBathroomListingDetails.text.toString().toIntOrNull()?:0
        val areaFromUI = binding.etAreaListingDetails.text.toString().toDoubleOrNull()?:0.0
        val descriptionFromUI = binding.etDescriptionListingDetails.text.toString()
        val address = binding.etAddressListingDetails.text.toString()
        val rentFromUI = binding.etRentListingDetails.text.toString().toDoubleOrNull()?:0.0
        val isAvailable = binding.isAvailableListingDetails.isChecked

        // Error handling
        if (
            numOfBedrooms <= 0 || numOfkitchens <= 0 || numOfbathrooms <= 0 || areaFromUI <= 0.0 ||
            descriptionFromUI.isEmpty() || address.isEmpty() || rentFromUI <= 0.0
        ) {
            Snackbar.make(binding.root, "Please fill out all the fields.", Snackbar.LENGTH_LONG).show()
            return
        }

        // update current listing object
        currListingObj.apply {
            propertyType = PropertyType.fromDisplayName(selectedPropertyTypeName)
            ownerInfo = userObj as User
            numberOfBedroom = numOfBedrooms
            numberOKitchen = numOfkitchens
            numberOfBathroom = numOfbathrooms
            area = areaFromUI
            description = descriptionFromUI
            propertyAddress = address
            rent = rentFromUI
            available = isAvailable
        }

        Log.d("currListingObj", "onCreate: currListingObj after updating: $currListingObj\n")

        // convert the list back to JSON and save it to SharedPreferences
        val gson = Gson()
        val updatedListJson = gson.toJson(myListingsList)
        editor.putString(userObj!!.userId, updatedListJson)
        editor.apply()
    }

}