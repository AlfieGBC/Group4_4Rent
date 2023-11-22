package com.pp.a4rent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.pp.a4rent.databinding.ActivityMainBinding
import com.pp.a4rent.screens.RentalsPostListActivity

import com.pp.a4rent.screens.AccountActivity
import com.pp.a4rent.screens.LoginActivity
import com.pp.a4rent.screens.RegisterActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG : String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

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

        binding.btnSearchToronto.setOnClickListener {
            this.goToTorontoRentals()
        }

        binding.btnSearch.setOnClickListener {
            this.goToRentalsPostList()
        }

        binding.btnSearchVancouver.setOnClickListener {
            this.goToVancouverRentals()
        }

        binding.btnSearchWinnipeg.setOnClickListener {
            this.goToWinnipegRentals()
        }
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

//            val check = rentalPostListIntent.getStringExtra("SEARCH_KEYWORD_FROM_ET")
//            Log.d("TAG", "$check")

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

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.menu_item_post_rental -> {
                Log.d(TAG, "onOptionsItemSelected: Post Rental option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@MainActivity, RentalFormActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d(TAG, "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
//                val sidebarIntent = Intent(this@MainActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

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
            else -> super.onOptionsItemSelected(item)
        }
    }
}