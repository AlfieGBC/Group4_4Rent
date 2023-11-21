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
import com.pp.a4rent.LoginActivity
import com.pp.a4rent.ProfileActivity
import com.pp.a4rent.R
import com.pp.a4rent.SignUpActivity
import com.pp.a4rent.databinding.ActivityRentalPostDetailBinding
import com.pp.a4rent.models.PropertyRental

class RentalPostDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityRentalPostDetailBinding
    private var rowPosition:Int = -1
    private lateinit var singleRentalPostDetail : PropertyRental
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_rental_post_detail)

        this.binding = ActivityRentalPostDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.menuToolbar)
        // Change the title
        supportActionBar?.title = "4Rent"

        // For changing the color of overflow icon
        // Get the drawable for the overflow icon
        val drawable = this.binding.menuToolbar.overflowIcon

        // Apply a tint to change the color of the overflow icon
        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.white))
            binding.menuToolbar.overflowIcon = wrappedDrawable
        }

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
            binding.tvRentalAddress.text = singleRentalPostDetail.propertyAddress
            //binding.ivRentalPost. = singleRentalPostDetail.imageFilename
            binding.tvRentalBeds.text = singleRentalPostDetail.numberOfBedroom.toString()
            binding.tvRentalBaths.text = singleRentalPostDetail.numberOfBathroom.toString()
            binding.tvRentalArea.text = singleRentalPostDetail.area.toString()
            binding.tvRentalPropertyType.text = singleRentalPostDetail.propertyType
            binding.tvRentalDescription.text = singleRentalPostDetail.description
            binding.tvRentalAvailable.text = singleRentalPostDetail.available.toString()
            binding.tvRentalRent.text = singleRentalPostDetail.rent.toString()
            binding.tvOwnerInfo.setText("Owner Name: ${singleRentalPostDetail.ownerInfo.name}\n" +
                    "Owner email: ${singleRentalPostDetail.ownerInfo.email}\n" +
                    "Owner Phone number: ${singleRentalPostDetail.ownerInfo.phoneNumber}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.menu_item_post_rental -> {
                Log.d("TAG", "onOptionsItemSelected: Post Rental option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@RentalPostDetailActivity, ProfileActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
//                val sidebarIntent = Intent(this@MainActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_signup -> {
                Log.d("TAG", "onOptionsItemSelected: Sign Up option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@RentalPostDetailActivity, SignUpActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_login -> {
                Log.d("TAG", "onOptionsItemSelected: Log In option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@RentalPostDetailActivity, LoginActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}