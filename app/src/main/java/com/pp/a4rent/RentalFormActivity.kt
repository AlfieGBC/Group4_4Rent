package com.pp.a4rent

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.pp.a4rent.databinding.ActivityRentalFormBinding
import com.pp.a4rent.models.PropertyRental
import com.pp.a4rent.models.PropertyType

class RentalFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRentalFormBinding
    private lateinit var sharedPref: SharedPreferences
    var propertiesList = mutableListOf<PropertyRental>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menu)

        val propertyTypeAdapter = ArrayAdapter<String>(this, R.layout.spinner_item_property_types, PropertyType.displayNames)
        binding.propertyTypeSpinner.adapter = propertyTypeAdapter

        binding.btnPublish.setOnClickListener {
            // Get data from UI
            val propertyType = binding.propertyTypeSpinner.selectedItem
            val numOfBedrooms = binding.etNumOfBedroom.text.toString().toIntOrNull()?:0
            val numOfkitchens = binding.etNumOfKitchen.text.toString().toIntOrNull()?:0
            val numOfbathrooms = binding.etNumOfBathroom.text.toString().toIntOrNull()?:0
            val description = binding.etDescription.text.toString()
            val address = binding.etAddress.text.toString()
            val rent = binding.etRent.text.toString().toDoubleOrNull()?:0.0
            val isAvailable = binding.isAvailable.isChecked

            Log.d("userInput", "onCreate: propertyType: $propertyType\n" +
                    "numOfBedrooms: $numOfBedrooms\n" +
                    "numOfkitchens: $numOfkitchens\n" +
                    "numOfbathrooms: $numOfbathrooms\n" +
                    "description: $description\n" +
                    "address: $address\n" +
                    "rent: $rent\n" +
                    "isAvailable: $isAvailable")

            // Error handling
            if (
                numOfBedrooms <= 0 || numOfkitchens <= 0 || numOfbathrooms <= 0 ||
                description.isEmpty() || address.isEmpty() || rent <= 0.0
            ) {
                Snackbar.make(binding.root, "Please fill out all the fields.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

//            val owner = Owner()
//            val propertyToAdd = PropertyRental(propertyType, owner, numOfBedrooms, numOfkitchens, numOfbathrooms, description, address, rent, isAvailable, image)
//            propertiesList.add(propertyToAdd)

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
}