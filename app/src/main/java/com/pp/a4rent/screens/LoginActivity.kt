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

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val TAG = this.javaClass.canonicalName
    private lateinit var prefs: SharedPreferences

    private lateinit var firebaseAuth : FirebaseAuth

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

//        binding.loginButton.setOnClickListener {
//            val email = binding.email.text.toString()
//            val password = binding.password.text.toString()
//
//            val userJson = sharedPreferences.getString(email, "")
//            if (userJson != "") {
//                val gson = Gson()
//                val user = gson.fromJson(userJson, User::class.java)
//                if (user.password == password) {
//                    Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
//
//                    // Redirect users to different profile pages based on their role
//                    if (user.role == "Landlord"){
//
//                        // If a user is Landlord
//                        val intent = Intent(this, ProfileActivity::class.java)
//                        intent.putExtra("extra_userObj", user)
//                        startActivity(intent)
//                    } else {
//
//                        // If a user is Tenant
//                        val intent = Intent(this, TenantAccountActivity::class.java)
//                        intent.putExtra("user", userJson)
//                        startActivity(intent)
//                    }
//                } else {
//                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.tvSignUpLink.setOnClickListener(this)

        binding.returnBack.setOnClickListener(this)
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
                    getUserRoleFromDatabase(email) { userRole ->
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

       // fun to retrieve the user roles from the database
        private fun getUserRoleFromDatabase(email: String, callback: (String) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userRole = document.getString("role")
                        if (userRole != null) {
                            // Invoke the callback with the user's role
                            callback.invoke(userRole)
                            // Exit the loop once role is found
                            return@addOnSuccessListener
                        }
                    }
                    // If no role found, callback with a default role
                    callback.invoke("guest")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting user role", exception)
                    // Invoke the callback with a default role in case of failure
                    callback.invoke("guest")
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
//        val intent = Intent(this, TenantProfileInfoActivity::class.java)
        startActivity(intent)
    }

    private fun goToLandlordAccount() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }


    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}