package com.pp.a4rent.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.auth.FirebaseAuth
import com.pp.a4rent.MainActivity
import com.pp.a4rent.MyListingsActivity
import com.pp.a4rent.R
import com.pp.a4rent.RentalFormActivity
import com.pp.a4rent.databinding.ActivityUserProfileInfoBinding
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.PropertyRepository
import com.pp.a4rent.repositories.UserRepository

class UserProfileInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileInfoBinding
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var userRepository: UserRepository
    private lateinit var rentalPropertyRepository: PropertyRepository

    private var loggedInUserEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tenant_profile_info)

        this.binding = ActivityUserProfileInfoBinding.inflate(layoutInflater)
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

        this.userRepository = UserRepository(applicationContext)
        this.rentalPropertyRepository = PropertyRepository(applicationContext)

        // initiate shared preference
        sharedPrefs = this.getSharedPreferences(packageName, Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()

        if (sharedPrefs.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
            userRepository.getUser(loggedInUserEmail)
        }

        this.binding.btnUpdateProfile.setOnClickListener {
            userRepository.updateUserProfile(User(
                firstName = this.binding.etFirstName.text.toString(),
                lastName = this.binding.etLastName.text.toString(),
                email = this.binding.etEmail.text.toString(),
                password = this.binding.etPassword.text.toString(),
                phoneNumber = this.binding.etPhoneNumber.text.toString(),
                role = this.binding.tvRoleData.text.toString()
            ))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        userRepository.currentUser.observe(this) { user ->
            if (user != null) {
                this.binding.tvRoleData.setText(user.role)
                this.binding.etFirstName.setText(user.firstName)
                this.binding.etLastName.setText(user.lastName)
                this.binding.etEmail.setText(user.email)
                this.binding.etPassword.setText(user.password)
                this.binding.etPhoneNumber.setText(user.phoneNumber)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu_options, menu)

        // checks user is logged in or not
        if (loggedInUserEmail.isNotEmpty()) {
            rentalPropertyRepository.getUserRoleFromDatabase(loggedInUserEmail) { userRole ->
                Log.d("TAG", "user role: $userRole")

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
                Log.d("TAG", "onOptionsItemSelected: Post Rental option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, MainActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }
            R.id.menu_item_blog -> {
                Log.d("TAG", "onOptionsItemSelected: Blog option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, BlogListActivity::class.java)
                startActivity(sidebarIntent)

                return true
            }

            R.id.mi_tenant_favourite -> {
                Log.d("TAG", "onOptionsItemSelected: Favourite option is selected")

                // navigate to 2nd screen
                val sidebarIntent = Intent(this, TenantAccountActivity::class.java)
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

            R.id.mi_post_rental -> {
                val intent = Intent(this, RentalFormActivity::class.java)
                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
                intent.putExtra("USER_EMAIL", "NA")
                startActivity(intent)
                return true
            }
            R.id.mi_my_account -> {

                val intent = Intent(this, UserProfileInfoActivity::class.java)
                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
                intent.putExtra("USER_EMAIL", "NA")
                startActivity(intent)
                return true
            }
            R.id.mi_my_listings -> {

                // pass through the user object
                val intent = Intent(this, MyListingsActivity::class.java)
                loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
                intent.putExtra("USER_EMAIL", "NA")
                startActivity(intent)
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