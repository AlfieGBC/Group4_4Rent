package com.pp.a4rent.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.pp.a4rent.MainActivity

import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import com.pp.a4rent.databinding.ActivityTenantAccountBinding
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.PropertyRepository

class TenantAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTenantAccountBinding
    private var TAG = "Tenant_Account"
    private lateinit var rentalPropertyAdapter: RentalsPostAdapter
    private lateinit var favRentalPropertyArrayList : ArrayList<Property>
    private lateinit var propertyRepository: PropertyRepository
    private var loggedInUserEmail = ""

    // TODO: Shared Preferences variables
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor

    // TODO: Get the current fruit data source
//    var favRentalPostsList:MutableList<PropertyRental> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTenantAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // display menu bar on the screen
        setSupportActionBar(this.binding.tenantMenuToolbar)
        // Change the title
        supportActionBar?.title = "4Rent"

        // For changing the color of overflow icon
        // Get the drawable for the overflow icon
        val drawable = this.binding.tenantMenuToolbar.overflowIcon

        // Apply a tint to change the color of the overflow icon
        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.white))
            binding.tenantMenuToolbar.overflowIcon = wrappedDrawable
        }

//      Initialize SharedPreference and Editor instance
        this.sharedPreferences = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)

        if (sharedPreferences.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()
        }



//
//
//        // Get existing rental list from SharedPreferences
//        this.prefEditor = this.sharedPreferences.edit()

//        val gson = Gson()
//        val userJson = intent.getStringExtra("user")
//        Log.d("TAG", "user ${userJson}")
//        var resultsFromSP: String? = null
//        var user: User? = null
//        if(userJson != null) {
//            user = gson.fromJson(userJson, User::class.java)
//            resultsFromSP = sharedPreferences.getString("KEY_RENTALS_DATASOURCE"+user.userId, "")
//            Log.d("TAG", "Incoming result ${"KEY_RENTALS_DATASOURCE"+user.userId}")
//            Log.d("TAG", "Incoming result2 ${resultsFromSP}")
//        }
//
//
//        if (resultsFromSP == null || resultsFromSP == "" ) {
//            // - if no, we should create a brand new list of fruits
//            // do nothing!
//        } else {
//            // if yes, convert the string back to list of rentals
//            val typeToken = object : TypeToken<List<PropertyRental>>() {}.type
//            // convert the string back to a list
//            val rentalsList = gson.fromJson<List<PropertyRental>>(resultsFromSP, typeToken)
//
//            // replace this screen's savedRentals variable with whatever came from the sp
//
//            favRentalPostsList.addAll(rentalsList.toMutableList())
//
//
//            Log.d("TAG", "Finally ${favRentalPostsList}")
//        }


        // setup adapter
        favRentalPropertyArrayList = ArrayList()
        rentalPropertyAdapter = RentalsPostAdapter(
            this,
            favRentalPropertyArrayList,
            {pos -> rowClicked(pos) },
            { pos -> favButtonClicked(pos) },
            favRentalPropertyArrayList
        )

        // setup rv
        binding.rvFavItems.adapter = rentalPropertyAdapter
        binding.rvFavItems.layoutManager = LinearLayoutManager(this)
        binding.rvFavItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        Log.d("TAG", "test: $favRentalPropertyArrayList")

        propertyRepository = PropertyRepository(applicationContext)


        binding.btnDeleteAll.setOnClickListener {

            // it deletes all the properties from the fav list
            propertyRepository.deleteAllPropertyFromFavList()

        }

    }

    override fun onResume() {
        super.onResume()

        propertyRepository.getAllPropertiesFromFavList()

        propertyRepository.allPropertiesInFavList.observe(this) { rentalsList ->
            if (rentalsList != null) {
                favRentalPropertyArrayList.clear()
                favRentalPropertyArrayList.addAll(rentalsList)
                rentalPropertyAdapter.notifyDataSetChanged()

            }
        }
    }

    // rv:  Row click handler
    fun rowClicked(rowPosition: Int){

        var selectedRental: Property = favRentalPropertyArrayList.get(rowPosition)

        // snackbar
        val snackbar = Snackbar.make(binding.root, "${selectedRental.toString()}, for row${rowPosition}", Snackbar.LENGTH_LONG)
        snackbar.show()

        // navigate to rental details page
        val intent = Intent(this, RentalPostDetailActivity::class.java)
        intent.putExtra("ROW_RENTAL_POST_DETAIL_POSITION", rowPosition)

        Log.d("TAG", "${favRentalPropertyArrayList.get(rowPosition)}")

        startActivity(intent)

    }

    // rv: Favorite button click handler
    fun favButtonClicked(position:Int) {

        val favRentalToDelete = favRentalPropertyArrayList.get(position)
        propertyRepository.deletePropertyFromFavList(favRentalToDelete)

        // After deleting the property, adapter must be notified
        rentalPropertyAdapter.notifyDataSetChanged()

        val snackbar = Snackbar.make(binding.root, "Favorite ${position} is deleted", Snackbar.LENGTH_LONG)
        snackbar.show()

        Log.d(TAG, "Check login : $loggedInUserEmail")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)


        // checks user is logged in or not
        if (loggedInUserEmail.isNotEmpty()) {
            propertyRepository.getUserRoleFromDatabase(loggedInUserEmail) { userRole ->
                Log.d(TAG, "user role: $userRole")

                // Show different menu options to the users based on their role
                if (userRole == "tenant") {
                    menuInflater.inflate(R.menu.tenant_profile_options, menu)
                } else if (userRole == "Landlord") {
                    menuInflater.inflate(R.menu.landlord_profile_options, menu)
                }

            }
        } else {
            menuInflater.inflate(R.menu.guest_menu_options, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_home -> {
                Log.d("TAG", "onOptionsItemSelected: Home option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, MainActivity::class.java)
                loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()
                sidebarIntent.putExtra("USER_EMAIL", "NA")

                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
               val sidebarIntent = Intent(this@TenantAccountActivity, BlogListActivity::class.java)
               startActivity(sidebarIntent)

                return true
            }


            R.id.mi_tenant_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Favourite option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@TenantAccountActivity, TenantAccountActivity::class.java)

                loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()
                sidebarIntent.putExtra("USER_EMAIL", "NA")

                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_profile -> {
                Log.d("TAG", "onOptionsItemSelected: Tenant Profile option is selected")

                // navigate to 2nd screen
                val sidebarTenantIntent = Intent(this@TenantAccountActivity, UserProfileInfoActivity::class.java)

                loggedInUserEmail = sharedPreferences.getString("USER_EMAIL", "NA").toString()
                sidebarTenantIntent.putExtra("USER_EMAIL", "NA")

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
            else -> super.onOptionsItemSelected(item)
        }

    }

}