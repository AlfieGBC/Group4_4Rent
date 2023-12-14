package com.pp.a4rent.screens
 
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.pp.a4rent.MainActivity
import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import com.pp.a4rent.databinding.ActivityRentalsPostListBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.repositories.PropertyRepository

import android.widget.RadioGroup
import com.pp.a4rent.listeners.OnRentalPostClickListener

class RentalsPostListActivity : AppCompatActivity(), OnRentalPostClickListener {

    private lateinit var binding: ActivityRentalsPostListBinding
    private val TAG : String = "TorontoRentalsActivity"

    // Made the adapter global
    private lateinit var rentalPropertyAdapter: RentalsPostAdapter
    private lateinit var rentalPropertyRepository: PropertyRepository

    private lateinit var searchedRentalsList: ArrayList<Property>
    private lateinit var favRentalPropertyArrayList: ArrayList<Property>
    private var loggedInUserEmail = ""

    private var receiveSearchInput: String? = null

    // Shared Preferences variables
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityRentalsPostListBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.menuToolbar)
        // Change the title
        supportActionBar?.apply {
            title = "4Rent"
        }

        // For changing the color of overflow icon
        // Get the drawable for the overflow icon
        val drawable = this.binding.menuToolbar.overflowIcon

        // Apply a tint to change the color of the overflow icon
        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.white))
            binding.menuToolbar.overflowIcon = wrappedDrawable
        }

        // initialize
        searchedRentalsList = ArrayList()
        favRentalPropertyArrayList = ArrayList()

        rentalPropertyRepository = PropertyRepository(applicationContext)

        // Initialize SharedPreference
        sharedPrefs = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)

        if (sharedPrefs.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
        }

        rentalPropertyRepository.getAllPropertiesFromFavList()

        rentalPropertyRepository.allPropertiesInFavList.observe(this) { rentalsList ->
            if (rentalsList != null) {
                favRentalPropertyArrayList.clear()
                favRentalPropertyArrayList.addAll(rentalsList)

            }
        }

        // search system
        receiveSearchInput = intent.getStringExtra("FILTER_DATA_EXTRA")

        if (receiveSearchInput != null) {
            Log.d("TAG", "list: receiveSearchInput ${receiveSearchInput}")
        }

        val toggle: RadioGroup = findViewById(R.id.rg_toggle)
        toggle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_map -> {
                    val intent = Intent(this, MapActivity::class.java)
                    intent.putExtra("FILTER_DATA_EXTRA", receiveSearchInput)
                    startActivity(intent)
                }
                R.id.rb_list -> {
                    setContentView(binding.root)
                }
            }
        }

        // setup adapter
        // Update adapter to accept a list of rentals
        rentalPropertyAdapter = RentalsPostAdapter(
            this,
            // pass the rental list data to the adapter
            searchedRentalsList,
            this,
            this,
            favRentalPropertyArrayList
            )

        // setup rv
        binding.rvItems.adapter = rentalPropertyAdapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun onResume() {
        super.onResume()

        rentalPropertyRepository.getAllProperties()

        rentalPropertyRepository.allProperties.observe(this) { rentalList ->
            if (rentalList != null) {
                searchedRentalsList.clear()
                searchedRentalsList.addAll(rentalList)
                applyFilter()
            }
        }
    }

    private fun updateUIAfterFiltering(filteredProperties: List<Property>) {
        rentalPropertyAdapter.updateList(filteredProperties)

        // Update the count TextView
        val resultCount = filteredProperties.size
        binding.tvSearchResultCount.text = "${resultCount} Apartments for rent in ${receiveSearchInput}"
        rentalPropertyAdapter.notifyDataSetChanged()
    }

    private fun applyFilter() {
        val filteredProperties = if (receiveSearchInput != null) {
            // Filter properties based on search criteria
            searchedRentalsList.filter { property ->
                val addressString = "${property.propertyAddress.street}, ${property.propertyAddress.city}, ${property.propertyAddress.province}"
                addressString.contains(receiveSearchInput!!, ignoreCase = true)
            }
        } else {
            emptyList()
        }

        updateUIAfterFiltering(filteredProperties)
    }


    override fun onRentalPropertySelected(property: Property) {
        val mainIntent = Intent(this, RentalPostDetailActivity::class.java)
        mainIntent.putExtra("EXTRA_PROPERTY", property)
        startActivity(mainIntent)
    }

    // rv: Favorite button click handler
    override fun favButtonClicked(favRentals: Property) {
        // checks user is logged in or not,
        // - if yes, navigate user to short-listed rentals page (favourite page)

        if (!loggedInUserEmail.isNotEmpty()) {
            this.rentalPropertyAdapter.notifyDataSetChanged()
            val userIntent = Intent(this@RentalsPostListActivity, LoginActivity::class.java)
            startActivity(userIntent)
        } else {
            rentalPropertyRepository.isPropertyExistInFavList(favRentals){ exists ->
                if (exists) {
                    rentalPropertyRepository.deletePropertyFromFavList(favRentals)
                    val snackbar = Snackbar.make(binding.root, "Removed from Fav Rental List", Snackbar.LENGTH_LONG)
                    snackbar.show()
                } else {
                    rentalPropertyRepository.addPropertyToFavList(favRentals)
                    val snackbar = Snackbar.make(binding.root, "Added to Favourite Rental List", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }

            Log.d(TAG, "FAV Rental To add ${favRentals}")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)

        // checks user is logged in or not
        if (loggedInUserEmail.isNotEmpty()) {
            rentalPropertyRepository.getUserRoleFromDatabase(loggedInUserEmail) { userRole ->
                Log.d(TAG, "user role: $userRole")

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
                Log.d("TAG", "onOptionsItemSelected: Home option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, MainActivity::class.java)

                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
                sidebarIntent.putExtra("USER_EMAIL", "NA")

                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d(TAG, "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
               val sidebarIntent = Intent(this@RentalsPostListActivity, BlogListActivity::class.java)
               startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_signup -> {
                Log.d(TAG, "onOptionsItemSelected: Sign Up option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@RentalsPostListActivity, RegisterActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_login -> {
                Log.d(TAG, "onOptionsItemSelected: Log In option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@RentalsPostListActivity, LoginActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Favourite option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, TenantAccountActivity::class.java)

                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
                sidebarIntent.putExtra("USER_EMAIL", "NA")

                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_profile -> {
                Log.d("TAG", "onOptionsItemSelected: Tenant Profile option is selected")

                // navigate to 2nd screen
                val sidebarTenantIntent = Intent(this, UserProfileInfoActivity::class.java)

                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
                sidebarTenantIntent.putExtra("USER_EMAIL", "NA")

                startActivity(sidebarTenantIntent)

                return true
            }

            R.id.mi_logout -> {
                // navigate to 2nd screen

                Log.d("TAG", "onOptionsItemSelected: Sign Out option is selected ${sharedPrefs.contains("USER_EMAIL")} ${sharedPrefs.edit().remove("USER_EMAIL").apply()}")
                if (sharedPrefs.contains("USER_EMAIL")) {
                    sharedPrefs.edit().remove("USER_EMAIL").apply()
                }
                if (sharedPrefs.contains("USER_PASSWORD")) {
                    sharedPrefs.edit().remove("USER_PASSWORD").apply()
                }
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}


