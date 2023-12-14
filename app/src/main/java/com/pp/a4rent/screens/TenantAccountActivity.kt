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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.pp.a4rent.MainActivity

import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import com.pp.a4rent.databinding.ActivityTenantAccountBinding
import com.pp.a4rent.listeners.OnRentalPostClickListener
import com.pp.a4rent.models.Property
import com.pp.a4rent.repositories.PropertyRepository
import java.io.Serializable

class TenantAccountActivity : AppCompatActivity(), OnRentalPostClickListener {
    private lateinit var binding: ActivityTenantAccountBinding
    private var TAG = "Tenant_Account"
    private lateinit var rentalPropertyAdapter: RentalsPostAdapter
    private lateinit var favRentalPropertyArrayList : ArrayList<Property>
    private lateinit var propertyRepository: PropertyRepository
    private var loggedInUserEmail = ""

    // TODO: Shared Preferences variables
    lateinit var sharedPreferences: SharedPreferences

    // TODO: Get the current fruit data source

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


        // setup adapter
        favRentalPropertyArrayList = ArrayList()
        rentalPropertyAdapter = RentalsPostAdapter(
            this,
            favRentalPropertyArrayList,
            this,
            this,
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
    override fun onRentalPropertySelected(property: Property) {
        val mainIntent = Intent(this, RentalPostDetailActivity::class.java)
        mainIntent.putExtra("EXTRA_EXPENSE", property as Serializable)
        startActivity(mainIntent)
    }

    override fun favButtonClicked(favRentals: Property) {
        // checks user is logged in or not,
        // - if yes, navigate user to short-listed rentals page (favourite page)

        propertyRepository.deletePropertyFromFavList(favRentals)

        // After deleting the property, adapter must be notified
        rentalPropertyAdapter.notifyDataSetChanged()

        val snackbar = Snackbar.make(binding.root, "Favorite is deleted", Snackbar.LENGTH_LONG)
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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}