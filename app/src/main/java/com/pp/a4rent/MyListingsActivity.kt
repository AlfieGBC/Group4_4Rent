package com.pp.a4rent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pp.a4rent.adapters.MyListingsAdapter
import com.pp.a4rent.databinding.ActivityMyListingsBinding
import com.pp.a4rent.models.PropertyRental

class MyListingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingsBinding
    private var myListingsList = listOf<PropertyRental>()
    private lateinit var adapter: MyListingsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menu)

        // set up the adapter
        this.adapter = MyListingsAdapter(myListingsList)
        binding.rvMyListings.adapter = adapter
        binding.rvMyListings.layoutManager = LinearLayoutManager(this)
        binding.rvMyListings.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.landlord_profile_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_home_page -> {
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
                val intent = Intent(this, ProfileActivity::class.java)
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