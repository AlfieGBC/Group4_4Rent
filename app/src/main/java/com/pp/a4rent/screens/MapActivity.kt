package com.pp.a4rent.screens

import com.pp.a4rent.R
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pp.a4rent.databinding.ActivityMapBinding
import android.util.Log
import android.location.Geocoder
import android.view.Menu
import android.widget.RadioGroup
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.pp.a4rent.repositories.PropertyRepository

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var TAG = this@MapActivity.toString()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var propertyRepository: PropertyRepository

    // Shared Preferences variables
    private lateinit var sharedPrefs: SharedPreferences
    private var receiveSearchInput: String? = null

    private var loggedInUserEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.menuToolbar)
        // Change the title
        supportActionBar?.apply {
            title = "4Rent"
        }

//        // For changing the color of overflow icon
//        // Get the drawable for the overflow icon
//        val drawable = this.binding.menuToolbar.overflowIcon
//
//        // Apply a tint to change the color of the overflow icon
//        if (drawable != null) {
//            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
//            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.white))
//            binding.menuToolbar.overflowIcon = wrappedDrawable
//        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // initialize PropertyRepository and get attributes
        propertyRepository = PropertyRepository(this)
        propertyRepository.getAllProperties()

        receiveSearchInput = intent.getStringExtra("FILTER_DATA_EXTRA")
        if (receiveSearchInput != null) {
            // Handle the search criteria and filter the rentals accordingly
            // Update the UI based on the filtered rentals
            Log.d("TAG", "map: receiveSearchInput ${receiveSearchInput}")
        }

        // switch view
        val toggle: RadioGroup = findViewById(R.id.rg_toggle)
        toggle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_map -> {
                    setContentView(binding.root)
                }
                R.id.rb_list -> {
                    val intent = Intent(this, RentalsPostListActivity::class.java)
                    intent.putExtra("FILTER_DATA_EXTRA", receiveSearchInput)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        propertyRepository.allProperties.observe(this) { properties ->
            Log.d("MapActivity", "Number of properties received: ${properties.size}")

            val filteredProperties = if (receiveSearchInput != null) {
                properties.filter { property ->
                    val addressString = "${property.propertyAddress.street}, ${property.propertyAddress.city}, ${property.propertyAddress.province}"
                    addressString.contains(receiveSearchInput!!, ignoreCase = true)
                }
            } else {
                emptyList()
            }

            val resultCount = filteredProperties.size
            binding.tvSearchResultCount.setText("${resultCount} Apartments for rent in ${receiveSearchInput}")

            Log.d("MapActivity", "Filtered properties: $filteredProperties")

            for (property in filteredProperties) {
                val address =
                    "${property.propertyAddress.street}, ${property.propertyAddress.city}, ${property.propertyAddress.province}"
                val location = getLocationFromAddress(address)
                Log.d("MapActivity", "location: $location")

                location?.let {
                    mMap.addMarker(MarkerOptions().position(it).title(address))
                }
            }

            // move view to the first location
            filteredProperties.firstOrNull()?.let {
                getLocationFromAddress(it.propertyAddress.street)?.let { firstLocation ->
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15.0f))
                }
            }
        }

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isTrafficEnabled = false

        val uiSettings = googleMap.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = false
    }

    private fun getLocationFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(this)

        try {
            val addressList = geocoder.getFromLocationName(address, 1)

            if (addressList != null && addressList.isNotEmpty()) {
                val latitude = addressList[0].latitude
                val longitude = addressList[0].longitude
                return LatLng(latitude, longitude)
            } else {
                Log.e(TAG, "No location found for the given address.")
                return null
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error converting address to LatLng: ${ex.message}")
            return null
        }
    }


//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.home_menu_options, menu)
//
//        // checks user is logged in or not
//        if (loggedInUserEmail.isNotEmpty()) {
//            propertyRepository.getUserRoleFromDatabase(loggedInUserEmail) { userRole ->
//                Log.d(TAG, "user role: $userRole")
//
//                // Show different menu options to the users based on their role
//                if (userRole == "tenant") {
//                    menuInflater.inflate(R.menu.tenant_profile_options, menu)
//                } else if (userRole == "landlord") {
//                    menuInflater.inflate(R.menu.landlord_profile_options, menu)
//                }
//
//            }
//        } else {
//            menuInflater.inflate(R.menu.guest_menu_options, menu)
//        }
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        return when(item.itemId) {
//            R.id.menu_item_home -> {
//                Log.d("TAG", "onOptionsItemSelected: Home option is selected")
//
//                // navigate to 2nd screen
//                val sidebarIntent = Intent(this, MainActivity::class.java)
//
//                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
//                sidebarIntent.putExtra("USER_EMAIL", "NA")
//
//                startActivity(sidebarIntent)
//
//                return true
//            }
//            R.id.menu_item_blog -> {
//                Log.d(TAG, "onOptionsItemSelected: Blog option is selected")
//
//                // navigate to 2nd screen
//               val sidebarIntent = Intent(this@MapActivity, BlogListActivity::class.java)
//               startActivity(sidebarIntent)
//
//                return true
//            }
//            R.id.menu_item_signup -> {
//                Log.d(TAG, "onOptionsItemSelected: Sign Up option is selected")
//
//                // navigate to 2nd screen
//                val sidebarIntent = Intent(this@MapActivity, RegisterActivity::class.java)
//                startActivity(sidebarIntent)
//
//                return true
//            }
//            R.id.menu_item_login -> {
//                Log.d(TAG, "onOptionsItemSelected: Log In option is selected")
//
//                // navigate to 2nd screen
//                val sidebarIntent = Intent(this@MapActivity, LoginActivity::class.java)
//                startActivity(sidebarIntent)
//
//                return true
//            }
//
//            R.id.mi_tenant_favourite -> {
//                Log.d("TAG", "onOptionsItemSelected: Favourite option is selected")
//
//                // navigate to 2nd screen
//                val sidebarIntent = Intent(this, TenantAccountActivity::class.java)
//
//                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
//                sidebarIntent.putExtra("USER_EMAIL", "NA")
//
//                startActivity(sidebarIntent)
//
//                return true
//            }
//
//            R.id.mi_tenant_profile -> {
//                Log.d("TAG", "onOptionsItemSelected: Tenant Profile option is selected")
//
//                // navigate to 2nd screen
//                val sidebarTenantIntent = Intent(this, UserProfileInfoActivity::class.java)
//
//                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
//                sidebarTenantIntent.putExtra("USER_EMAIL", "NA")
//
//                startActivity(sidebarTenantIntent)
//
//                return true
//            }
//
//            R.id.mi_logout -> {
//                // navigate to 2nd screen
//
//                Log.d("TAG", "onOptionsItemSelected: Sign Out option is selected ${sharedPrefs.contains("USER_EMAIL")} ${sharedPrefs.edit().remove("USER_EMAIL").apply()}")
//                if (sharedPrefs.contains("USER_EMAIL")) {
//                    sharedPrefs.edit().remove("USER_EMAIL").apply()
//                }
//                if (sharedPrefs.contains("USER_PASSWORD")) {
//                    sharedPrefs.edit().remove("USER_PASSWORD").apply()
//                }
//                FirebaseAuth.getInstance().signOut()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                return true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

}
