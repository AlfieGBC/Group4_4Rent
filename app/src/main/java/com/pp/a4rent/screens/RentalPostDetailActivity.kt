package com.pp.a4rent.screens

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pp.a4rent.databinding.ActivityRentalPostDetailBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.repositories.UserRepository

class RentalPostDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityRentalPostDetailBinding
    private lateinit var sharedPrefs: SharedPreferences
    private var loggedInUserEmail = ""
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_rental_post_detail)

        this.binding = ActivityRentalPostDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        // Initialize SharedPreference
        sharedPrefs = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)
        userRepository = UserRepository(applicationContext)

        if (sharedPrefs.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
            userRepository.getUser(loggedInUserEmail)
        }

        val singleRentalPost = intent.getSerializableExtra("EXTRA_PROPERTY") as? Property

        // get the intent data
        if(singleRentalPost != null) {

            // populate the rental post text
            binding.tvRentalAddress.setText("${singleRentalPost.propertyAddress.street}, ${singleRentalPost.propertyAddress.city}, ${singleRentalPost.propertyAddress.province}, ${singleRentalPost.propertyAddress.country}")
            binding.tvRentalBeds.setText("${singleRentalPost.numberOfBedroom.toString()} bedrooms")
            binding.tvRentalBaths.setText("${singleRentalPost.numberOfBathroom.toString()}bathrooms")
            binding.tvRentalKitchens.setText("${singleRentalPost.numberOKitchen.toString()}kitchens")
            binding.tvRentalArea.setText("${singleRentalPost.area.toString()} sq. feet")
            binding.tvRentalPropertyType.setText("${singleRentalPost.propertyType}")
            binding.tvRentalDescription.setText("${singleRentalPost.description}")
            binding.tvRentalRent.setText("Rent: $${singleRentalPost.rent.toString()}")


            // owner info
            userRepository.currentUser.observe(this) { user ->
                if (user != null) {
                    this.binding.tvOwnerInfo.setText("Owner Name: ${user.firstName} ${user.lastName}\n" +
                    "Owner email: ${user.email}\n" +
                    "Owner Phone number: ${user.phoneNumber}")
                }
            }

            // update image
            val imagename = singleRentalPost.imageFilename
            val res = resources.getIdentifier(imagename, "drawable", this.packageName)
            this.binding.ivRentalPost.setImageResource(res)

            // availability
            if (singleRentalPost.available) {
                binding.tvRentalAvailable.text = "Availability: YES"
            } else {
                binding.tvRentalAvailable.text = "Availability: NO"
            }

        }
    }


}