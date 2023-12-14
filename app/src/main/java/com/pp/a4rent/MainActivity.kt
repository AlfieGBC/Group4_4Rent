package com.pp.a4rent

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.pp.a4rent.databinding.ActivityMainBinding
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.PropertyRepository
import com.pp.a4rent.screens.RentalsPostListActivity

import com.pp.a4rent.screens.LoginActivity
import com.pp.a4rent.screens.RegisterActivity

import com.pp.a4rent.screens.BlogListActivity
import com.pp.a4rent.screens.TenantAccountActivity
import com.pp.a4rent.screens.UserProfileInfoActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG : String = "MainActivity"

    private lateinit var sharedPreferences: SharedPreferences
    private var loggedInUserEmail = ""
    private lateinit var propertyRepository: PropertyRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityMainBinding.inflate(layoutInflater)
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

        // Initialize SharedPreference
        sharedPreferences = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)
        propertyRepository = PropertyRepository(applicationContext)

        if (sharedPreferences.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()

            Log.d(TAG, "Check login first: $loggedInUserEmail")
        }

        // For search button
        binding.btnSearch.setOnClickListener {
            this.goToRentalsPostList()
        }

        // For searching based on location
        // Toronto
        binding.btnSearchToronto.setOnClickListener {
            this.goToTorontoRentals()
        }

        // Vancouver
        binding.btnSearchVancouver.setOnClickListener {
            this.goToVancouverRentals()
        }

        // Winnipeg
        binding.btnSearchWinnipeg.setOnClickListener {
            this.goToWinnipegRentals()
        }


        Log.d(TAG, "Check login : $loggedInUserEmail")
    }

    // Function to navigate users to the list of Rentals post page
    // Shows list of rentals in Toronto area
    fun goToTorontoRentals() {

        // navigate to 2nd screen
        val torontoRentalsIntent = Intent(this@MainActivity, RentalsPostListActivity::class.java)
        torontoRentalsIntent.putExtra("FILTER_DATA_EXTRA", "Toronto")
        startActivity(torontoRentalsIntent)
    }

    // Function to navigate users to the list of Rentals post page
    // Shows list of rentals in Vancouver area
    fun goToVancouverRentals() {

        // navigate to 2nd screen
        val vancouverRentalsIntent = Intent(this@MainActivity, RentalsPostListActivity::class.java)
        vancouverRentalsIntent.putExtra("FILTER_DATA_EXTRA", "Vancouver")

        startActivity(vancouverRentalsIntent)
    }

    // Function to navigate users to the list of Rentals post page
    // Shows list of rentals in Winnipeg area
    fun goToWinnipegRentals() {

        // navigate to 2nd screen
        val winnipegRentalsIntent = Intent(this@MainActivity, RentalsPostListActivity::class.java)
        winnipegRentalsIntent.putExtra("FILTER_DATA_EXTRA", "Winnipeg")

        startActivity(winnipegRentalsIntent)
    }

    fun goToRentalsPostList() {
        // get the input from edit text
        val etSearchKeyword : String = binding.etSearch.text.toString()

        // error handling
        if (etSearchKeyword.isEmpty()) {
            this.binding.tvErrorMsg.setText("ERROR: Search field must be filled in!")
            return
        } else {
            // navigate to search result screen
            val rentalPostListIntent = Intent(this@MainActivity, RentalsPostListActivity::class.java)
            rentalPostListIntent.putExtra("FILTER_DATA_EXTRA", etSearchKeyword)

            startActivity(rentalPostListIntent)
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "user returned to MainActivity!")
        binding.etSearch.setText("")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)

        Log.d(TAG, "Check login : $loggedInUserEmail")
        // checks user is logged in or not
        if (loggedInUserEmail.isNotEmpty()) {
            propertyRepository.getUserRoleFromDatabase(loggedInUserEmail) { userRole ->
                Log.d(TAG, "user role testing: $userRole")

                // Show different menu options to the users based on their role

                if (userRole == "tenant") {
                    menuInflater.inflate(R.menu.tenant_profile_options, menu)
                } else if (userRole == "landlord") {
                    menuInflater.inflate(R.menu.landlord_profile_options, menu)
                }

            }
        } else {
            menuInflater.inflate(R.menu.guest_menu_options, menu)
        }

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.menu_item_home -> {
                Log.d(TAG, "onOptionsItemSelected: Home option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@MainActivity, MainActivity::class.java)
               loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()
                sidebarIntent.putExtra("USER_EMAIL", "NA")
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d(TAG, "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@MainActivity, BlogListActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            R.id.menu_item_signup -> {
                Log.d(TAG, "onOptionsItemSelected: Sign Up option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_login -> {
                Log.d(TAG, "onOptionsItemSelected: Log In option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Favourite option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, TenantAccountActivity::class.java)

                loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()
                sidebarIntent.putExtra("USER_EMAIL", "NA")
                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_profile -> {
                Log.d("TAG", "onOptionsItemSelected: Tenant Profile option is selected")

                // navigate to 2nd screen
                val sidebarTenantIntent = Intent(this, UserProfileInfoActivity::class.java)

                startActivity(sidebarTenantIntent)

                return true
            }
            R.id.mi_logout -> {
                // navigate to 2nd screen

                Log.d("TAG", "onOptionsItemSelected: Sign Out option is selected ${sharedPreferences.contains("USER_EMAIL")} ${sharedPreferences.edit().remove("USER_EMAIL").apply()}")
                if (sharedPreferences.contains("USER_EMAIL")) {
                    sharedPreferences.edit().remove("USER_EMAIL").apply()
                }
                if (sharedPreferences.contains("USER_PASSWORD")) {
                    sharedPreferences.edit().remove("USER_PASSWORD").apply()
                }
                FirebaseAuth.getInstance().signOut()
//                this@UserProfileInfoActivity.finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.mi_post_rental -> {
                val intent = Intent(this, RentalFormActivity::class.java)

                startActivity(intent)
                return true
            }
            R.id.mi_my_account -> {
                val intent = Intent(this, UserProfileInfoActivity::class.java)

                startActivity(intent)
                return true
            }
            R.id.mi_my_listings -> {
                val intent = Intent(this, MyListingsActivity::class.java)

                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}