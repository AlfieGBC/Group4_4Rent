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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pp.a4rent.MainActivity

import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import com.pp.a4rent.databinding.ActivityTenantAccountBinding
import com.pp.a4rent.models.PropertyRental
import com.pp.a4rent.models.User

import com.pp.a4rent.screens.BlogListActivity

class TenantAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTenantAccountBinding

    lateinit var adapter: RentalsPostAdapter

    // TODO: Shared Preferences variables
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor

    // TODO: Get the current fruit data source
    var favRentalPostsList:MutableList<PropertyRental> = mutableListOf()

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

        // Initialize SharedPreference and Editor instance
        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)

        // Get existing rental list from SharedPreferences
        this.prefEditor = this.sharedPreferences.edit()


        val resultsFromSP = sharedPreferences.getString("KEY_RENTALS_DATASOURCE", "")
        Log.d("TAG", "Incoming result ${resultsFromSP}")


        if (resultsFromSP == "") {
            // - if no, we should create a brand new list of fruits
            // do nothing!
        } else {
            // if yes, convert the string back to list of rentals
            val gson = Gson()
            val typeToken = object : TypeToken<List<PropertyRental>>() {}.type
            // convert the string back to a list
            val rentalsList = gson.fromJson<List<PropertyRental>>(resultsFromSP, typeToken)

            // replace this screen's savedRentals variable with whatever came from the sp
//            favRentalPostsList = rentalsList.toMutableList()
            favRentalPostsList.addAll(rentalsList.toMutableList())
//            adapter.notifyDataSetChanged()

            Log.d("TAG", "Finally ${favRentalPostsList}")
        }

        // setup adapter
        // TODO: Update adapter to accept a list of fruits
        adapter = RentalsPostAdapter(
            favRentalPostsList,
            {pos -> rowClicked(pos) },
            {pos -> favButtonClicked(pos)}

        )

        // setup rv
        binding.rvFavItems.adapter = adapter
        binding.rvFavItems.layoutManager = LinearLayoutManager(this)
        binding.rvFavItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        Log.d("TAG", "test: $favRentalPostsList")


        binding.btnDeleteAll.setOnClickListener {
            prefEditor.remove("KEY_RENTALS_DATASOURCE")

            // commit our changes
            prefEditor.apply()

            // update the RV to show that there are no more items
            favRentalPostsList.clear()
            adapter.notifyDataSetChanged()
        }


    }

    // rv:  Row click handler
    fun rowClicked(rowPosition: Int){

        var selectedRental: PropertyRental = favRentalPostsList.get(rowPosition)
        // snackbar
        val snackbar = Snackbar.make(binding.root, "${selectedRental.toString()}, for row${rowPosition}", Snackbar.LENGTH_LONG)
        snackbar.show()

        // navigate to rental details page
        val intent = Intent(this, RentalPostDetailActivity::class.java)
        intent.putExtra("ROW_RENTAL_POST_DETAIL_POSITION", rowPosition)

        // send the details of the rental post to next screen
        // rentalDatasource -> PropertyRental class must be Serializable interface or protocol
        intent.putExtra("ROW_RENTAL_POST_DETAIL",   favRentalPostsList.get(rowPosition))


        Log.d("TAG", "${favRentalPostsList.get(rowPosition)}")


        startActivity(intent)

    }

    // rv: Favorite button click handler
    fun favButtonClicked(position:Int) {
        val snackbar = Snackbar.make(binding.root, "Favorite ${position}", Snackbar.LENGTH_LONG)
        snackbar.show()

        favRentalPostsList.removeAt(position)
        adapter.notifyDataSetChanged()

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
        return when(item.itemId){
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
               val sidebarIntent = Intent(this@TenantAccountActivity, BlogListActivity::class.java)
               startActivity(sidebarIntent)

                return true
            }


            R.id.mi_tenant_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this@TenantAccountActivity, TenantAccountActivity::class.java)
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
                val sidebarTenantIntent = Intent(this@TenantAccountActivity, TenantProfileInfoActivity::class.java)
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