package com.pp.a4rent.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.MyListingsActivity
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityMyListingDetailsBinding
import com.pp.a4rent.models.Address
import com.pp.a4rent.models.Geo
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.PropertyType
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.PropertyRepository
import java.util.Locale

class MyListingDetailsActivity : AppCompatActivity() {
    private var TAG = this@MyListingDetailsActivity.toString()
    private lateinit var binding: ActivityMyListingDetailsBinding
    private lateinit var propertyRepository: PropertyRepository

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.propertyRepository = PropertyRepository(applicationContext)

        // set up spinner
        val propertyTypeAdapter = ArrayAdapter(this, R.layout.spinner_item_property_types, PropertyType.displayNames)
        binding.propertyTypeSpinnerListingDetails.adapter = propertyTypeAdapter

        if (intent != null){

            // get property obj
            val currListingObj = if (intent.hasExtra("extra_property_obj")) {
                intent.getSerializableExtra("extra_property_obj") as? Property
            } else {null}

            if (currListingObj != null){

                // auto fill the views
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
                    etAddressListingDetails.setText(currListingObj.propertyAddress.street)
                    etAddressCityListingDetails.setText(currListingObj.propertyAddress.city)
                    etAddressProvinceListingDetails.setText(currListingObj.propertyAddress.province)
                    etAddressCountryListingDetails.setText(currListingObj.propertyAddress.country)
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
                    btnDeleteClicked(currListingObj)
                    finish()
                }

            } else {
                Log.e(TAG, "onCreate: propertyObj is null.", )
            }
        }
    }

    private fun getSpinnerPositionForPropertyType(propertyType: PropertyType): Int {
        val displayNames = PropertyType.displayNames
        return displayNames.indexOf(propertyType.displayName)
    }

    private fun btnDeleteClicked(currListingObj: Property){

        // delete from property collection
        propertyRepository.deleteProperty(currListingObj)

        // delete from sub collections
        propertyRepository.deletePropertyFromPropertyList(currListingObj)

        // TODO

        Snackbar.make(binding.root, "Deleted successfully!", Snackbar.LENGTH_LONG).show()
        val intent = Intent(this, MyListingsActivity::class.java)
        startActivity(intent)
    }

    private fun btnSaveClicked(currListingObj: Property){
        // Get data from UI
        val selectedPropertyTypeName = binding.propertyTypeSpinnerListingDetails.selectedItem.toString()
        val numOfBedrooms = binding.etNumOfBedroomListingDetails.text.toString().toIntOrNull()?:0
        val numOfKitchens = binding.etNumOfKitchenListingDetails.text.toString().toIntOrNull()?:0
        val numOfBathrooms = binding.etNumOfBathroomListingDetails.text.toString().toIntOrNull()?:0
        val areaFromUI = binding.etAreaListingDetails.text.toString().toDoubleOrNull()?:0.0
        val descriptionFromUI = binding.etDescriptionListingDetails.text.toString()
        val rentFromUI = binding.etRentListingDetails.text.toString().toDoubleOrNull()?:0.0
        val isAvailable = binding.isAvailableListingDetails.isChecked

        val street = binding.etAddressListingDetails.text.toString()
        val city = binding.etAddressCityListingDetails.text.toString()
        val province = binding.etAddressProvinceListingDetails.text.toString()
        val country = binding.etAddressCountryListingDetails.text.toString()

        // Error handling
        if (
            numOfBedrooms <= 0 || numOfKitchens <= 0 || numOfBathrooms <= 0 || areaFromUI <= 0.0 || rentFromUI <= 0.0 ||
            descriptionFromUI.isEmpty() || street.isEmpty() || city.isEmpty() || province.isEmpty() || country.isEmpty()
        ) {
            Snackbar.make(binding.root, "Please fill out all the fields.", Snackbar.LENGTH_LONG).show()
            return
        }

        // re-calculate geo info
//        val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())
//        var latitude = 0.0
//        var longitude = 0.0
//        val addressToConvert = "$street $city $province $country"
//        try {
//            val searchResults:MutableList<android.location.Address>? = geocoder.getFromLocationName(addressToConvert, 1)
//            if (searchResults == null) {
//                Log.e(TAG, "searchResults variable is null")
//                return
//            }
//
//            if (searchResults.size == 0) {
//                Log.d(TAG, "publishBtnClicked: Search results are empty.")
//                return
//            } else {
//                val foundLocation: android.location.Address = searchResults.get(0)
//                latitude = foundLocation.latitude
//                longitude = foundLocation.longitude
//                var message = "Coordinates are: ${foundLocation.latitude}, ${foundLocation.longitude}"
//                Log.d(TAG, message)
//            }
//        } catch(ex:Exception) {
//            Log.e(TAG, "Error encountered while getting coordinate location.")
//            return
//        }

        // update current listing object
        val propertyToUpdate = Property(
            propertyId = currListingObj.propertyId,
            propertyType = PropertyType.fromDisplayName(selectedPropertyTypeName),
            ownerInfo = currListingObj.ownerInfo,
            numberOfBedroom = numOfBedrooms,
            numberOKitchen = numOfKitchens,
            numberOfBathroom = numOfBathrooms,
            area = areaFromUI,
            description = descriptionFromUI,
            propertyAddress = Address(street, city, province, country),
            rent = rentFromUI,
            available = isAvailable,
        )
        Log.d(TAG, "btnSaveClicked: propertyToUpdate: propertyToUpdate after updating: $propertyToUpdate")

        // update property in propertyList
        propertyRepository.updatePropertyInPropertyList(propertyToUpdate)

        // update property in database
        propertyRepository.updateProperty(propertyToUpdate)

        // TODO: update property in favList

        Snackbar.make(binding.root, "Updated successfully!", Snackbar.LENGTH_LONG).show()
        val intent = Intent(this, MyListingsActivity::class.java)
        startActivity(intent)
    }
}