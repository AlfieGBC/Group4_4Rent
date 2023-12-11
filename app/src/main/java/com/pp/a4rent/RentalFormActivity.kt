package com.pp.a4rent

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.databinding.ActivityRentalFormBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.PropertyType
import com.pp.a4rent.models.User

import com.pp.a4rent.screens.LoginActivity
import com.pp.a4rent.screens.BlogListActivity
import java.util.Locale


class RentalFormActivity : AppCompatActivity(), View.OnClickListener {
    private var TAG = this@RentalFormActivity.toString()
    private lateinit var binding: ActivityRentalFormBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var propertiesList = mutableListOf<Property>()
    private var userObj: User? = null
    private val gson = Gson()

    // Device location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // permissions array
    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // showing the permissions dialog box & its result
    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { resultsList ->
        Log.d(TAG, resultsList.toString())

        var allPermissionsGrantedTracker = true
        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                allPermissionsGrantedTracker = false
            }
        }

        if (allPermissionsGrantedTracker == true) {
            Snackbar.make(binding.root, "All permissions granted", Snackbar.LENGTH_LONG).show()
            getDeviceLocation()

        } else {
            Snackbar.make(binding.root, "Some permissions NOT granted", Snackbar.LENGTH_LONG).show()
            //handlePermissionDenied()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // instantiate the fusedLocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // initiate shared preference
        sharedPreferences = getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // set up menu
        setSupportActionBar(this.binding.menu)

        // set up spinner
        val propertyTypeAdapter =
            ArrayAdapter(this, R.layout.spinner_item_property_types, PropertyType.displayNames)
        binding.propertyTypeSpinner.adapter = propertyTypeAdapter

        if (intent != null) {

            // get the user object from intent
            userObj = if (intent.hasExtra("extra_userObj")) {
                intent.getSerializableExtra("extra_userObj") as User
            } else {null}

            // if userObj doesn't exist, the uer is NOT logged in
            if (userObj == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {

                // when buttons clicked
                binding.btnPublish.setOnClickListener(this)
                binding.btnGetPosition.setOnClickListener(this)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnGetPosition -> {
                // Check for permissions & do resulting actions
                multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            }
            binding.btnPublish -> {
                publishBtnClicked()
            }
        }
    }

    private fun publishBtnClicked(){
        // Get data from UI
        val selectedPropertyTypeName = binding.propertyTypeSpinner.selectedItem.toString()
        val numOfBedrooms = binding.etNumOfBedroom.text.toString().toIntOrNull() ?: 0
        val numOfKitchens = binding.etNumOfKitchen.text.toString().toIntOrNull() ?: 0
        val numOfBathrooms = binding.etNumOfBathroom.text.toString().toIntOrNull() ?: 0
        val area = binding.etArea.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.etDescription.text.toString()
        val address = binding.etAddress.text.toString()
        val rent = binding.etRent.text.toString().toDoubleOrNull() ?: 0.0
        val isAvailable = binding.isAvailable.isChecked

        // Error handling
        if (
            numOfBedrooms <= 0 || numOfKitchens <= 0 || numOfBathrooms <= 0 || area <= 0.0 ||
            description.isEmpty() || address.isEmpty() || rent <= 0.0
        ) {
            Snackbar.make(
                binding.root,
                "Please fill out all the fields.",
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        // get user object from shared preference
//            val userJson = sharedPreferences.getString(email, "")
//            if (userJson == ""){
//                // if user not logged in
//                Snackbar.make(binding.root, "Please login first.", Snackbar.LENGTH_LONG).show()
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                return@setOnClickListener
//            }
//
//            userObj = gson.fromJson(userJson, User::class.java)

        // get myListings list from sharedPreference
        val myListingsListJson = sharedPreferences.getString(userObj!!.userId, "")
        if (myListingsListJson != "") {
            val typeToken = object : TypeToken<List<Property>>() {}.type
            propertiesList =
                gson.fromJson<List<Property>>(myListingsListJson, typeToken).toMutableList()
        }

        // add property to the list
        val propertyType = PropertyType.fromDisplayName(selectedPropertyTypeName)
        val propertyToAdd = Property(
            propertyType, userObj!!, numOfBedrooms, numOfKitchens, numOfBathrooms, area,
            description, address, rent, isAvailable
        )
        propertiesList.add(propertyToAdd)
        Log.d(
            "propertiesList", "onCreate: propertiesList: $propertiesList\n" +
                    "propertiesList size: ${propertiesList.size}"
        )

        // convert list back to string, using user ID as KEY to store a list of listings
        val propertiesListJson = gson.toJson(propertiesList)
        editor.putString(userObj!!.userId, propertiesListJson)
        editor.apply()

        val intent = Intent(this, MyListingsActivity::class.java)
        intent.putExtra("extra_userObj", userObj)
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
                val sidebarIntent =
                    Intent(this@RentalFormActivity, BlogListActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_post_rental -> {
                // pass through the user object
                val intent = Intent(this, RentalFormActivity::class.java)
                intent.putExtra("extra_userObj", userObj)
                startActivity(intent)
                return true
            }

            R.id.mi_my_account -> {

                // pass through the user object
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("extra_userObj", userObj)
                startActivity(intent)
                return true
            }

            R.id.mi_my_listings -> {

                // pass through the user object
                val intent = Intent(this, MyListingsActivity::class.java)
                intent.putExtra("extra_userObj", userObj)
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

    private fun getDeviceLocation() {
        // CHECK permission for FINE_LOCATION and COARSE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // ask again for permission
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        // if all permission granted
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                // Output the location
                val message = "The device is located at: ${location.latitude}, ${location.longitude}"
                Log.d(TAG, message)
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

                // get user address from the geo code
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                try {
                    val searchResults:MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (searchResults == null) {
                        Log.e(TAG, "getting Street Address: searchResults is NULL ")
                        return@addOnSuccessListener
                    }

                    if (searchResults.size == 0) {
                        Log.d(TAG, "No search results for location (${location.latitude}, ${location.longitude}).")
                    } else {

                        val matchingAddress: Address = searchResults[0]
                        Log.d(TAG, "Search results found.")
                        Log.d(TAG, "Country: " + matchingAddress.countryName)
                        Log.d(TAG, "City: " + matchingAddress.locality)
                        Log.d(TAG, "Street: " + matchingAddress.thoroughfare)
                        Log.d(TAG, "Street number: " + matchingAddress.subThoroughfare)

                        // prefill the UI elements for address
                        binding.apply {
                            etAddress.setText("${matchingAddress.subThoroughfare} ${matchingAddress.thoroughfare}")
                            etAddressCity.setText(matchingAddress.locality)
                            etAddressProvince.setText(matchingAddress.adminArea)
                            etAddressCountry.setText(matchingAddress.countryName)
                        }
                    }
                } catch(ex:Exception) {
                    Log.e(TAG, "Error encountered while getting coordinate location.")
                    Log.e(TAG, ex.toString())
                }
            }
    }

    private fun handlePermissionDenied() {
        binding.apply {
            etAddress.setText("Please grant permission for location access.")
            btnGetPosition.isEnabled = false
        }
    }

}