package com.pp.a4rent.screens

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.MainActivity
import com.pp.a4rent.ProfileActivity
import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import com.pp.a4rent.databinding.ActivityRentalsPostListBinding
import com.pp.a4rent.models.Owner
import com.pp.a4rent.models.PropertyRental
import com.pp.a4rent.models.User

import com.pp.a4rent.screens.BlogListActivity

class RentalsPostListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentalsPostListBinding

    private val TAG : String = "TorontoRentalsActivity"

    // Made the adapter global
    private lateinit var adapter: RentalsPostAdapter

    // made this mutable because we know that later on, we have to add fruits to it
       private var rentalDatasource:MutableList<PropertyRental> = mutableListOf<PropertyRental>(
        PropertyRental(1,"condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "Humber Line 123 Street, NY","Toronto", "N2M 8C9", 2500.0, true, "peter",false),
        PropertyRental(2, "condo", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","Bloor St , NY","North York", "S2M 3C9",2500.0, true, "amy",false ),
        PropertyRental(3,"condo", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", " Line 123 Street, Victoria, Vancouver","vancouver", "B2D 5N6", 2700.0, true, "alex",false),
        PropertyRental(4,"basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo", "12 vi Street, Van","Vancouver", "B2D 5N6",2300.0, true, "jane",false),
        PropertyRental(5,"condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "123 st abc, winnipeg", "Winnipeg","N4M 8C9",2500.0, true, "peter",false),
        PropertyRental(6,"condo", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","12 Keele Street, NY","North York", "N2M 8C9",2500.0, true, "amy",false),
        PropertyRental(7, "condo", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", "16 victoria st, BC","vancouver", "C2M 8C9",2500.0, true, "alex",false),
        PropertyRental(8,"basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo", "879 Islington av, Etobicoke", "toronto", "N2B 8C9",2500.0, true, "jane",false),
        PropertyRental(9,"condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "456 abc st, winnipeg","winnipeg", "N2W 6C6",2500.0, true, "peter",false),
        PropertyRental(10,"condo", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","78 jane st, north york","north york", "N2M 5C6",2500.0, true, "amy",false),
        PropertyRental(11,"condo", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", "45 dupont st, toronto","Toronto", "N2MH 4C9",2500.0, true, "alex",false),
        PropertyRental(12,"basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo","46 dufferin st, toronto", "toronto", "I2O 8C3",2500.0, true, "jane",false),
    )

    var searchedRentalsList: MutableList<PropertyRental> = mutableListOf()


    // Shared Preferences variables
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor

    // Get the current fruit data source
    var favouriteRentalPostsList:MutableList<PropertyRental> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityRentalsPostListBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.menuToolbar)
        // Change the title
        supportActionBar?.apply {
            title = "4Rent"
            setDisplayHomeAsUpEnabled(true)
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


        // Initialize SharedPreference and Editor instance
        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)

        // Get existing rental list from SharedPreferences
        this.prefEditor = this.sharedPreferences.edit()

        val gson = Gson()
        val userJson = intent.getStringExtra("user")
        Log.d("TAG", "check ${userJson}")
        var savedRentalsFromSP: String? = null
        if(userJson != null) {
            val user = gson.fromJson(userJson, User::class.java)
            savedRentalsFromSP = sharedPreferences.getString("KEY_RENTALS_DATASOURCE"+user.userId, "")
        }

        // get favourite rentals on activity create
        if (savedRentalsFromSP == null || savedRentalsFromSP == "") {
            // - if no, we should create a brand new list of fruits
            // do nothing!
        } else {
            // if yes, convert the string back to list of rentals
            val typeToken = object : TypeToken<List<PropertyRental>>() {}.type
            // convert the string back to a list
            val rentalsList = gson.fromJson<List<PropertyRental>>(savedRentalsFromSP, typeToken)

            Log.d("TAG", "checkinggggg hai ${favouriteRentalPostsList}")

            // replace this screen's savedRentals variable with whatever came from the sp
            favouriteRentalPostsList = rentalsList.toMutableList()

            Log.d("TAG", "heram hai ${favouriteRentalPostsList}")
        }

        /// list loop in each item -> check if it exists in favourite or not
        // if exists then set isFavourite = true
        for (rental in rentalDatasource) {
            for (fav in favouriteRentalPostsList) {
                Log.d("TAG", "REntal found ${rental.rentalID}")
                if (rental.rentalID == fav.rentalID) {
                    Log.d("TAG", "Favourite found ${rental.rentalID}")
                    rental.favourite = true
                }
            }
        }

        // TODO: Search System
        // get intent data from main activity
        // receive the search input data
        val receiveSearchInput = intent.getStringExtra("FILTER_DATA_EXTRA")

        if (receiveSearchInput != null) {
            Log.d("TAG", receiveSearchInput)

            var found = false;
            // get the data from the array
            for (rental in rentalDatasource) {
                for (fav in favouriteRentalPostsList) {
                    Log.d("TAG", "Rental found ${rental.rentalID}")
                    if (rental.rentalID == fav.rentalID) {
                        Log.d("TAG", "Favourite found ${rental.rentalID}")
                        rental.favourite = true
                    }
                }
                // check condition
                if (rental.propertyType.toLowerCase() == receiveSearchInput.toLowerCase() ||
                    rental.propertyAddress.toLowerCase() == receiveSearchInput.toLowerCase() ||
                    rental.postalCode.toLowerCase() == receiveSearchInput.toLowerCase() ||
                    rental.propertyType.toLowerCase().contains(receiveSearchInput.toLowerCase()) ||
                    rental.propertyAddress.toLowerCase().contains(receiveSearchInput.toLowerCase()) ||
                    rental.postalCode.toLowerCase().contains(receiveSearchInput.toString().toLowerCase()) ||
                    rental.city.toLowerCase() == receiveSearchInput.toLowerCase() ||
                    rental.city.toLowerCase().contains(receiveSearchInput.toLowerCase())
                    ) {

                    Log.d("TAG", "PAss")
                    found = true



                    // - if yes, then store that into a variable
                    val rentalToAdd = PropertyRental(
                        rental.rentalID,
                        rental.propertyType,
                        rental.ownerInfo,
                        rental.numberOfBedroom,
                        rental.numberOKitchen,
                        rental.numberOfBathroom,
                        rental.area,
                        rental.description,
                        rental.propertyAddress,
                        rental.postalCode,
                        rental.city,
                        rental.rent,
                        rental.available,
                        rental.imageFilename,
                        rental.favourite)

                    // and add that into the empty list
                    searchedRentalsList.add(rentalToAdd)

                    // setup adapter
                    // Update adapter to accept a list of rentals
                    this.adapter = RentalsPostAdapter(
                        // pass the rental list data to the adapter
                        searchedRentalsList,
                        {pos -> rowClicked(pos) },
                        {pos -> favButtonClicked(pos)}

                    )

                    // setup rv
                    binding.rvItems.adapter = adapter
                    binding.rvItems.layoutManager = LinearLayoutManager(this)
                    binding.rvItems.addItemDecoration(
                        DividerItemDecoration(
                            this,
                            LinearLayoutManager.VERTICAL
                        )
                    )

                }
            }

            if (!found)
            {
                binding.tvRentalErrorMsg.setText("Result not found!")
            }

        }


    }


    // rv:  Row click handler
    fun rowClicked(rowPosition: Int){

        // checks user is logged in or not
        val userJson = intent.getStringExtra("user")
        if (userJson == null) {
            val userIntent = Intent(this@RentalsPostListActivity, LoginActivity::class.java)
            startActivity(userIntent)
        } else {
            var selectedRental: PropertyRental = searchedRentalsList.get(rowPosition)

            // snackbar
            val snackbar = Snackbar.make(binding.root, "${selectedRental.toString()}, for row${rowPosition}", Snackbar.LENGTH_LONG)
            snackbar.show()

            // navigate to rental details page
            val intent = Intent(this, RentalPostDetailActivity::class.java)
            intent.putExtra("ROW_RENTAL_POST_DETAIL_POSITION", rowPosition)

            // send the details of the rental post to next screen
            // rentalDatasource -> PropertyRental class must be Serializable interface or protocol
            intent.putExtra("ROW_RENTAL_POST_DETAIL",   searchedRentalsList.get(rowPosition))
            // pass this info to next page, which is tenant profile info page
            intent.putExtra("user", userJson)

            Log.d("TAG", "${searchedRentalsList.get(rowPosition)}")


            startActivity(intent)
        }


    }

    // rv: Favorite button click handler
    fun favButtonClicked(position:Int) {

        var rentalsToAdd = searchedRentalsList.get(position)

        // checks user is logged in or not,
        // - if yes, navigate user to short-listed rentals page (favourite page)
        val userJson = intent.getStringExtra("user")
        if (userJson == null) {
            rentalsToAdd.favourite = false
            this.adapter.notifyDataSetChanged()
            val userIntent = Intent(this@RentalsPostListActivity, LoginActivity::class.java)
            startActivity(userIntent)
        } else {

        val snackbar = Snackbar.make(binding.root, "Added to Favourite List", Snackbar.LENGTH_LONG)
        snackbar.show()

//        rentalsToAdd.favourite = !rentalsToAdd.favourite

        rentalsToAdd.favourite = true
        // if rentalsToAdd already exists in favouriteRentalPostsList then ignore


        var rentalExists = false
        var favRentalIndex: Int? = null
        for (index in favouriteRentalPostsList.indices) {
            if (favouriteRentalPostsList[index].rentalID == rentalsToAdd.rentalID) {
                Log.d("TAG", "Rental already exist in the favourite list")
                rentalExists = true
                favRentalIndex = index
                break
            }
        }


        if (!rentalExists) {
            rentalsToAdd.favourite = true
            favouriteRentalPostsList.add(rentalsToAdd)
        } else {
            favouriteRentalPostsList.removeAt(favRentalIndex!!)
        }

        val gson = Gson()
        val listAsString = gson.toJson(favouriteRentalPostsList)
        val user = gson.fromJson(userJson, User::class.java)
        this.prefEditor.putString("KEY_RENTALS_DATASOURCE"+user.userId, listAsString)

        // commit the changes
        this.prefEditor.apply()

        Log.d("TAG", "testing this list: ${listAsString}")
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


