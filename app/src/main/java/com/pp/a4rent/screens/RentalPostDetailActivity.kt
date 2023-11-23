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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)


        // checks user is logged in or not
        val userJson = intent.getStringExtra("user")
        if (userJson != null) {
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)

            if (user.role == "Tenant") {
                menuInflater.inflate(R.menu.tenant_profile_options, menu)
            } else if (user.role == "Landlord") {
                menuInflater.inflate(R.menu.landlord_profile_options, menu)
            }

        } else {
            menuInflater.inflate(R.menu.guest_menu_options, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
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
//                val sidebarIntent = Intent(this@MainActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_signup -> {
                Log.d("TAG", "onOptionsItemSelected: Sign Up option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@RentalPostDetailActivity, RegisterActivity::class.java)
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

            R.id.mi_tenant_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, TenantAccountActivity::class.java)
                // get the user info from login page
                val userJson = intent.getStringExtra("user")
                // pass this info to next page, which is tenant profile info page
                sidebarIntent.putExtra("user", userJson)

                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_profile -> {
                Log.d("TAG", "onOptionsItemSelected: Tenant Profile option is selected")

                // navigate to 2nd screen
                val sidebarTenantIntent = Intent(this, TenantProfileInfoActivity::class.java)
                // get the user info from login page
                val userJson = intent.getStringExtra("user")
                // pass this info to next page, which is tenant profile info page
                sidebarTenantIntent.putExtra("user", userJson)
                startActivity(sidebarTenantIntent)

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
}