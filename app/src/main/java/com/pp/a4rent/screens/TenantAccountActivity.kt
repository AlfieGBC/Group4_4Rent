package com.pp.a4rent.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityTenantAccountBinding

class TenantAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTenantAccountBinding

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


//        val userJson = intent.getStringExtra("user")
//        if (userJson != null) {
//            val gson = Gson()
//            val user = gson.fromJson(userJson, User::class.java)
//
//            binding.firstName.text = user.firstName
//            binding.lastName.text = user.lastName
//            binding.email.text = user.email
//            binding.role.text = user.role
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tenant_profile_options, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.mi_tenant_profile -> {
                Log.d("TAG", "onOptionsItemSelected: Tenant Profile option is selected")

                // navigate to 2nd screen
                val sidebarTenantIntent = Intent(this@TenantAccountActivity, TenantProfileInfoActivity::class.java)
                val userJson = intent.getStringExtra("user")
                sidebarTenantIntent.putExtra("user", userJson)
                startActivity(sidebarTenantIntent)

                return true
            }
            R.id.mi_tenant_blog -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
//                val sidebarIntent = Intent(this@TenantAccountActivity, AccountActivity::class.java)
//                startActivity(sidebarIntent)

                return true
            }
            R.id.mi_tenant_logout -> {
                Log.d("TAG", "onOptionsItemSelected: Sign Up option is selected")

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}