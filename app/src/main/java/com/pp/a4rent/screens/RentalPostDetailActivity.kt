package com.pp.a4rent.screens

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.gson.Gson
import com.pp.a4rent.MainActivity
import com.pp.a4rent.ProfileActivity
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityRentalPostDetailBinding
import com.pp.a4rent.models.PropertyRental
import com.pp.a4rent.models.User

import com.pp.a4rent.screens.LoginActivity
import com.pp.a4rent.screens.RegisterActivity
import com.pp.a4rent.screens.BlogListActivity

class RentalPostDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityRentalPostDetailBinding
    private var rowPosition:Int = -1
    private lateinit var singleRentalPostDetail : PropertyRental
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_rental_post_detail)

        this.binding = ActivityRentalPostDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)


        val currentIntent = this@RentalPostDetailActivity.intent
        // get the intent data
        if(intent != null) {
            this.rowPosition = intent.getIntExtra("ROW_RENTAL_POST_DETAIL_POSITION", -1)
//            val singleRentalPostDetail = intent.getStringExtra("ROW_RENTAL_POST_DETAIL")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                singleRentalPostDetail =
                    currentIntent.getSerializableExtra("ROW_RENTAL_POST_DETAIL", PropertyRental::class.java)!!

            } else {
                // do casting to convert it to product type -> like as Product
                singleRentalPostDetail = currentIntent.getSerializableExtra("ROW_RENTAL_POST_DETAIL") as PropertyRental
            }

            // populate the rental post text
            binding.tvRentalAddress.setText("${singleRentalPostDetail.propertyAddress}")
            binding.tvRentalBeds.setText("${singleRentalPostDetail.numberOfBedroom.toString()}bedrooms")
            binding.tvRentalBaths.setText("${singleRentalPostDetail.numberOfBathroom.toString()}bathrooms")
            binding.tvRentalKitchens.setText("${singleRentalPostDetail.numberOKitchen.toString()}kitchens")
            binding.tvRentalArea.setText("${singleRentalPostDetail.area.toString()} sq. feet")
            binding.tvRentalPropertyType.setText("${singleRentalPostDetail.propertyType}")
            binding.tvRentalDescription.setText("${singleRentalPostDetail.description}")
            binding.tvRentalRent.setText("$${singleRentalPostDetail.rent.toString()}")

            // owner info
            binding.tvOwnerInfo.setText("Owner Name: ${singleRentalPostDetail.ownerInfo.name}\n" +
                    "Owner email: ${singleRentalPostDetail.ownerInfo.email}\n" +
                    "Owner Phone number: ${singleRentalPostDetail.ownerInfo.phoneNumber}")

            // update image
            val imagename = singleRentalPostDetail.imageFilename
            val res = resources.getIdentifier(imagename, "drawable", this.packageName)
            this.binding.ivRentalPost.setImageResource(res)

            // availability
            if (singleRentalPostDetail.available) {
                binding.tvRentalAvailable.text = "Availability: YES"
            } else {
                binding.tvRentalAvailable.text = "Availability: NO"
            }

        }
    }


}