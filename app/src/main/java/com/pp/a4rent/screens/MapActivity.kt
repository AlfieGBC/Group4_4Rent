package com.pp.a4rent.screens

import com.pp.a4rent.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pp.a4rent.databinding.ActivityMapBinding
import android.util.Log
import android.location.Geocoder
import android.widget.RadioGroup

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // display menu bar on the screen and change title
        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.apply {
            title = "4Rent"
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // initialize PropertyRepository and get attributes
        propertyRepository = PropertyRepository(this)
        propertyRepository.getAllProperties()

        // switch view
        val toggle: RadioGroup = findViewById(R.id.rg_toggle)
        toggle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_map -> {
                    setContentView(binding.root)
                }
                R.id.rb_list -> {
                    val intent = Intent(this, RentalsPostListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // add red points on the map
        propertyRepository.allProperties.observe(this) { properties ->
            Log.d("MapActivity", "Number of properties received: ${properties.size}")

            for (property in properties) {
                val address = property.propertyAddress.street + ", " + property.propertyAddress.city + ", " + property.propertyAddress.province
                val location = getLocationFromAddress(address)
                Log.d("MapActivity", "location: $location")

                location?.let {
                    mMap.addMarker(MarkerOptions().position(it).title(address))
                }
            }

            // move view to the first location
            properties.firstOrNull()?.let {
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
}
