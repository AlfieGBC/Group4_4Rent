package com.pp.a4rent

import android.content.Intent
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
import com.pp.a4rent.adapters.TorontoRentalsAdapter
import com.pp.a4rent.databinding.ActivityTorontoRentalsBinding
import com.pp.a4rent.models.Owner
import com.pp.a4rent.models.PropertyRental

class TorontoRentalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTorontoRentalsBinding
    private val TAG : String = "TorontoRentalsActivity"

    // Made the adapter global
    private lateinit var adapter: TorontoRentalsAdapter

    // made this mutable because we know that later on, we have to add fruits to it
    private var rentalDatasource:MutableList<PropertyRental> = mutableListOf<PropertyRental>(
        PropertyRental("condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, "cool condo", "abc", 2500.0, true, "peter"),
        PropertyRental("house", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, "cool condo","abc", 2500.0, true, "amy"),
        PropertyRental("apartment", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, "cool condo", "abc", 2500.0, true, "alex"),
        PropertyRental("basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, "cool condo", "abc", 2500.0, true, "jane"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_toronto_rentals)

        this.binding = ActivityTorontoRentalsBinding.inflate(layoutInflater)
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

        // setup adapter
        // Update adapter to accept a list of rentals
        this.adapter = TorontoRentalsAdapter(
            rentalDatasource,
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

    // rv:  Row click handler
    fun rowClicked(position:Int){

        var selectedRental:PropertyRental = rentalDatasource.get(position)
        // snackbar
        val snackbar = Snackbar.make(binding.root, "${selectedRental.toString()}", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    // rv: Favorite button click handler
    fun favButtonClicked(position:Int) {
        val snackbar = Snackbar.make(binding.root, "Favorite ${position}", Snackbar.LENGTH_LONG)
        snackbar.show()
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
                val sidebarIntent = Intent(this@TorontoRentalsActivity, ProfileActivity::class.java)
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
//                val sidebarIntent = Intent(this@MainActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_login -> {
                Log.d(TAG, "onOptionsItemSelected: Log In option is selected")

                // navigate to 2nd screen
//                val sidebarIntent = Intent(this@MainActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}