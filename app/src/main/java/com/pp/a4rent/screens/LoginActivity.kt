package com.pp.a4rent.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pp.a4rent.ProfileActivity
import com.pp.a4rent.MainActivity
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityLoginBinding
import com.pp.a4rent.repositories.PropertyRepository

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val TAG = this.javaClass.canonicalName
    private lateinit var prefs: SharedPreferences

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var propertyRepository: PropertyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebaseAuth
        this.firebaseAuth = FirebaseAuth.getInstance()

        prefs = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)

        binding.btnLogin.setOnClickListener(this)

        if (prefs.contains("USER_EMAIL")) {
            binding.etEmail.setText(this.prefs.getString("USER_EMAIL", ""))
        }
        if (prefs.contains("USER_PASSWORD")) {
            binding.etPassword.setText(this.prefs.getString("USER_PASSWORD", ""))
        }

        binding.tvSignUpLink.setOnClickListener(this)

        binding.returnBack.setOnClickListener(this)

        propertyRepository = PropertyRepository(applicationContext)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.btn_login -> {
                    Log.d(TAG, "onClick: Login button Clicked")
                    this.validateData()
                }
                R.id.tv_signUpLink -> {
                    Log.d(TAG, "onClick: SignUp link Clicked")
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
                R.id.returnBack -> {
                    Log.d(TAG, "onClick: Return Back link Clicked")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }
        }
    }

    private fun validateData() {
        var validData = true
        var email = ""
        var password = ""

        // email
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "Email Cannot be Empty"
            validData = false
        } else {
            email = binding.etEmail.text.toString()
        }

        // password
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPassword.error = "Password Cannot be Empty"
            validData = false
        } else {
            password = binding.etPassword.text.toString()
        }

        // valid data
        if (validData) {
            signIn(email, password)
        } else {
            Toast.makeText(this, "Please provide correct inputs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn(email: String, password: String) {
//         Login using FirebaseAuth

        this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    propertyRepository.getUserRoleFromDatabase(email) { userRole ->
                        Log.d(TAG, "LogIn: Login successfully")
                        Log.d(TAG, "user role: $userRole")

                        // Redirect users to different profile pages based on their role
                        when (userRole) {
                            "tenant" -> goToTenantAccount()
                            "landlord" -> goToLandlordAccount()
                            else -> {
                                Log.e(TAG, "Role of user is guest")
                                // Redirect to homepage
                                goToMain()
                            }
                        }

                        saveToPrefs(email, password)
                    }
                } else {
                    Log.d(TAG, "LogIn: Login failed : ${task.exception}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Authentication failed. Check the credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


        private fun saveToPrefs(email: String, password: String) {
        if (binding.swtRemember.isChecked) {
            prefs.edit().putString("USER_EMAIL", email).apply()
            prefs.edit().putString("USER_PASSWORD", password).apply()
        } else {
            if (prefs.contains("USER_EMAIL")) {
                prefs.edit().remove("USER_EMAIL").apply()
            }
            if (prefs.contains("USER_PASSWORD")) {
                prefs.edit().remove("USER_PASSWORD").apply()
            }
        }
    }

    private fun goToTenantAccount() {
        val intent = Intent(this, TenantAccountActivity::class.java)
        startActivity(intent)
    }

    private fun goToLandlordAccount() {
        val intent = Intent(this, UserProfileInfoActivity::class.java)
        startActivity(intent)
    }


    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}