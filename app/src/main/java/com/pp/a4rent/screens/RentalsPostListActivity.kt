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
import com.google.gson.Gson
import com.pp.a4rent.MainActivity
import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import com.pp.a4rent.databinding.ActivityRentalsPostListBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.PropertyRepository

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

class RentalsPostListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentalsPostListBinding
    private val TAG : String = "TorontoRentalsActivity"

    // Made the adapter global
    private lateinit var rentalPropertyAdapter: RentalsPostAdapter
    private lateinit var rentalPropertyRepository: PropertyRepository

    private lateinit var searchedRentalsList: ArrayList<Property>
    private lateinit var favRentalPropertyArrayList: ArrayList<Property>
    private var loggedInUserEmail = ""



    // made this mutable because we know that later on, we have to add rentals to it
//       private var rentalDatasource:MutableList<PropertyRental> = mutableListOf<PropertyRental>(
//        PropertyRental(1,PropertyType.APARTMENT, Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "Humber Line 123 Street, NY","Toronto", "N2M 8C9", 2500.0, true, "peter",false),
//        PropertyRental(2, PropertyType.CONDO, Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","Bloor St , NY","North York", "S2M 3C9",2500.0, true, "amy",false ),
//        PropertyRental(3,PropertyType.CONDO, Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", " Line 123 Street, Victoria, Vancouver","vancouver", "B2D 5N6", 2700.0, true, "alex",false),
//        PropertyRental(4,PropertyType.BASEMENT, Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo", "12 vi Street, Van","Vancouver", "B2D 5N6",2300.0, true, "jane",false),
//        PropertyRental(5,PropertyType.CONDO, Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "123 st abc, winnipeg", "Winnipeg","N4M 8C9",2500.0, true, "peter",false),
//        PropertyRental(6,PropertyType.APARTMENT, Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","12 Keele Street, NY","North York", "N2M 8C9",2500.0, true, "amy",false),
//        PropertyRental(7, PropertyType.APARTMENT, Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", "16 victoria st, BC","vancouver", "C2M 8C9",2500.0, true, "alex",false),
//        PropertyRental(8,PropertyType.HOUSE, Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo", "879 Islington av, Etobicoke", "toronto", "N2B 8C9",2500.0, true, "jane",false),
//        PropertyRental(9,PropertyType.CONDO, Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "456 abc st, winnipeg","winnipeg", "N2W 6C6",2500.0, true, "peter",false),
//        PropertyRental(10,PropertyType.CONDO, Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","78 jane st, north york","north york", "N2M 5C6",2500.0, true, "amy",false),
//        PropertyRental(11,PropertyType.BASEMENT, Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", "45 dupont st, toronto","Toronto", "N2MH 4C9",2500.0, true, "alex",false),
//        PropertyRental(12,PropertyType.HOUSE, Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo","46 dufferin st, toronto", "toronto", "I2O 8C3",2500.0, true, "jane",false),
//    )




//    // Shared Preferences variables
    private lateinit var sharedPrefs: SharedPreferences
//    lateinit var prefEditor: SharedPreferences.Editor

    // Get the current data source
//    var favouriteRentalPostsList:MutableList<Property> = mutableListOf()

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
        val receiveSearchInput = intent.getStringExtra("FILTER_DATA_EXTRA")

        if (receiveSearchInput != null) {
            // Handle the search criteria and filter the rentals accordingly
            // Update the UI based on the filtered rentals
            Log.d("TAG", "list: receiveSearchInput ${receiveSearchInput}")
        }

        // TODO: (Alfie) testing google map
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

//        // Get existing rental list from SharedPreferences
//        this.prefEditor = this.sharedPreferences.edit()
//
//        val gson = Gson()
//        val userJson = intent.getStringExtra("user")
//        Log.d("TAG", "check ${userJson}")
//        var savedRentalsFromSP: String? = null
//        if(userJson != null) {
//            val user = gson.fromJson(userJson, User::class.java)
//            savedRentalsFromSP = sharedPreferences.getString("KEY_RENTALS_DATASOURCE"+user.userId, "")
//        }
//
//        // get favourite rentals on activity create
//        if (savedRentalsFromSP == null || savedRentalsFromSP == "") {
//            // - if no, we should create a brand new list of fruits
//            // do nothing!
//        } else {
//            // if yes, convert the string back to list of rentals
//            val typeToken = object : TypeToken<List<PropertyRental>>() {}.type
//            // convert the string back to a list
//            val rentalsList = gson.fromJson<List<PropertyRental>>(savedRentalsFromSP, typeToken)
//
//            Log.d("TAG", "checkinggggg hai ${favouriteRentalPostsList}")
//
//            // replace this screen's savedRentals variable with whatever came from the sp
//            favouriteRentalPostsList = rentalsList.toMutableList()
//
//            Log.d("TAG", "heram hai ${favouriteRentalPostsList}")
//        }

        /// list loop in each item -> check if it exists in favourite or not
        // if exists then set isFavourite = true
//        for (rental in searchedRentalsList) {
////            for (fav in favouriteRentalPostsList) {
//                Log.d("TAG", "Rental found ${rental.propertyId}")
//                if (rental.favourite == false) {
//                    Log.d("TAG", "Favourite found ${rental.propertyId}")
//                    rental.favourite = true
////                }
//            }
//        }
//
//        // Search System
//        // get intent data from main activity
//        // receive the search input data
//        val receiveSearchInput = intent.getStringExtra("FILTER_DATA_EXTRA")
//
//        if (receiveSearchInput != null) {
//            Log.d("TAG", receiveSearchInput)
//
//            var found = false;
//            // get the data from the array
//            for (rental in rentalDatasource) {
//                for (fav in favouriteRentalPostsList) {
//                    Log.d("TAG", "Rental found ${rental.propertyId}")
//                    if (rental.propertyId == fav.propertyId) {
//                        Log.d("TAG", "Favourite found ${rental.propertyId}")
//                        rental.favourite = true
//                    }
//                }
//                // check condition
//                if (rental.propertyType.displayName.toLowerCase() == receiveSearchInput.toLowerCase() ||
//                    rental.propertyType.displayName.toLowerCase().contains(receiveSearchInput.toLowerCase()) ||
//                    rental.propertyAddress.city.toLowerCase() == receiveSearchInput.toLowerCase() ||
//                    rental.propertyAddress.city.toLowerCase().contains(receiveSearchInput.toLowerCase())
////                    rental.propertyAddress.postalCode.toLowerCase() == receiveSearchInput.toLowerCase() ||
////                    rental.propertyAddress.postalCode.toLowerCase().contains(receiveSearchInput.toString().toLowerCase()) ||
//                    ) {
//
//                    Log.d("TAG", "PAss")
//                    found = true
//
//                    // - if yes, then store that into a variable
//                    val rentalToAdd = Property(
//                        rental.propertyId,
//                        rental.propertyType,
//                        rental.ownerInfo,
//                        rental.numberOfBedroom,
//                        rental.numberOKitchen,
//                        rental.numberOfBathroom,
//                        rental.area,
//                        rental.description,
//                        rental.propertyAddress,
////                        rental.postalCode,
////                        rental.propertyAddress.city,
//                        rental.rent,
//                        rental.available,
//                        rental.geo,
//                        rental.imageFilename,
//                        rental.favourite)
//
//                    // and add that into the empty list
//                    searchedRentalsList.add(rentalToAdd)
//
                    // setup adapter
                    // Update adapter to accept a list of rentals
                    rentalPropertyAdapter = RentalsPostAdapter(
                        this,
                        // pass the rental list data to the adapter
                        searchedRentalsList,
                        {pos -> rowClicked(pos) },
                        {pos -> favButtonClicked(pos)},
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
//
//                }
//            }
//
//            if (!found)
//            {
//                binding.tvRentalErrorMsg.setText("Result not found!")
//            }
//
//        }

    }

    override fun onResume() {
        super.onResume()

        rentalPropertyRepository.getAllProperties()

        rentalPropertyRepository.allProperties.observe(this) { rentalList ->

            if (rentalList != null) {
                searchedRentalsList.clear()
                searchedRentalsList.addAll(rentalList)
                rentalPropertyAdapter.notifyDataSetChanged()
            }
        }

    }


    // rv:  Row click handler
    fun rowClicked(rowPosition: Int){

        // checks user is logged in or not
        if (!loggedInUserEmail.isNotEmpty()) {
            val userIntent = Intent(this@RentalsPostListActivity, LoginActivity::class.java)
            startActivity(userIntent)
        } else {
            var selectedRental: Property = searchedRentalsList.get(rowPosition)

            // snackbar
            val snackbar = Snackbar.make(binding.root, "${selectedRental.toString()}, for row${rowPosition}", Snackbar.LENGTH_LONG)
            snackbar.show()

            // navigate to rental details page
            val intent = Intent(this, RentalPostDetailActivity::class.java)
            intent.putExtra("ROW_RENTAL_POST_DETAIL_POSITION", rowPosition)

            // send the details of the rental post to next screen
            Log.d("TAG", "${searchedRentalsList.get(rowPosition)}")

            startActivity(intent)
        }


    }

    // rv: Favorite button click handler
    fun favButtonClicked(position:Int) {

        var favRentals = searchedRentalsList.get(position)

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
//                this@UserProfileInfoActivity.finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}


