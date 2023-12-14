package com.pp.a4rent.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pp.a4rent.MainActivity
import com.pp.a4rent.R
import com.pp.a4rent.databinding.ActivityRegisterBinding
import com.pp.a4rent.models.User
import com.pp.a4rent.repositories.UserRepository


class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegisterBinding
    private val TAG = "Register"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userRepository: UserRepository
    val minLength = 8
    val maxLength = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase auth
        this.firebaseAuth = FirebaseAuth.getInstance()

        this.userRepository = UserRepository(applicationContext)

        binding.btnRegister.setOnClickListener(this)

        binding.tvLoginLink.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        binding.tvReturnBack.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }
    }

//    private fun isEmailRegistered(email: String): Boolean {
//        return sharedPreferences.contains(email)
//    }

    override fun onClick(view: View?) {
        if (view != null) {
            when(view.id) {
                R.id.btn_register -> {
                    Log.d(TAG, "onClick: Create Account button Clicked")
                    validateData()
                }
            }
        }
    }

    private fun validateData() {
        var validData = true
        var firstname = ""
        var lastname = ""
        var email = ""
        var password = ""
        var phoneNumber = ""
        var role = ""

        // firstname
        if (binding.etFirstName.getText().toString().isEmpty()) {
            binding.etFirstName.setError("FirstName Cannot be Empty")
            validData = false
        } else {
            firstname = binding.etFirstName.getText().toString()
        }

        // lastname
        if (binding.etLastName.getText().toString().isEmpty()) {
            binding.etLastName.setError("LastName Cannot be Empty")
            validData = false
        } else {
            lastname = binding.etLastName.getText().toString()
        }

        // email
        if (binding.etEmail.getText().toString().isEmpty()) {
            binding.etEmail.setError("Email Cannot be Empty")
            validData = false
        }
//        else if (isEmailRegistered(email)) {
//            Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
//            validData = false
//        }
//        else if (!email.contains("@")) {
//            binding.etEmail.setError("Invalid email address")
////            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
//            validData = false
//        }
        else {
            email = binding.etEmail.getText().toString()
        }

        // password
        if (binding.etPassword.getText().toString().isEmpty()) {
            binding.etPassword.setError("Password Cannot be Empty")
            validData = false
        }
//        else if (password.length < minLength || password.length > maxLength) {
//            binding.etPassword.setError("Password must be between $minLength and $maxLength characters long")
////            Toast.makeText(this, "Password must be between $minLength and $maxLength characters long", Toast.LENGTH_SHORT).show()
//
//            validData = false
//        }
        else {

            if (binding.etConfirmPassword.getText().toString().isEmpty()) {
                binding.etConfirmPassword.setError("Confirm Password Cannot be Empty")
                validData = false
            } else {
                if (!binding.etPassword.getText().toString()
                        .equals(binding.etConfirmPassword.getText().toString())
                ) {
                    binding.etConfirmPassword.setError("Both passwords must be same")
                    validData = false
                } else {
                    password = binding.etPassword.getText().toString()
                    Log.d(TAG, "ERROR:  ${password}")
                }
            }
        }

        // phonenumber
        if (binding.etPhoneNumber.getText().toString().isEmpty()) {
            binding.etPhoneNumber.setError("Phone Number Cannot be Empty")
            validData = false
        } else {
            phoneNumber = binding.etPhoneNumber.getText().toString()
        }

        if (binding.rgRole.checkedRadioButtonId == -1) {
            binding.tvRoleErrMsg.setText("ERROR - Role cannot be empty!")
            validData = false
        } else {
            val selectedRadioButton: RadioButton = findViewById(binding.rgRole.checkedRadioButtonId)
            if (selectedRadioButton.id == R.id.rb_tenant) {
                role ="tenant"
            } else {
                role = "landlord"
            }
        }

        Log.d(TAG, "$validData")

        // valid data
        if (validData) {
            createAccount(firstname, lastname, email, password, phoneNumber, role)
        } else {
            Toast.makeText(this, "Please provide correct inputs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(firstname: String, lastname: String, email: String, password: String, phoneNumber: String, role : String) {
//        SignUp using FirebaseAuth

        Log.d(TAG, "$email $firstname $lastname $password $phoneNumber $role")

        this.firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful) {
                    // create user document with default profile info
                    val userToAdd = User(
                        userId = email,
                        firstName = firstname,
                        lastName = lastname,
                        email = email,
                        password = password,
                        phoneNumber = phoneNumber,
                        role = role,
                    )

                    Log.d(TAG, "User to add : $userToAdd")
                    this.userRepository.addUserToDB(userToAdd)

                    Log.d(TAG, "createAccount: User account successfully create with email $email")
                    saveToPrefs(email, password)
                    goToLogin()
                } else {
                    Log.d(TAG, "createAccount: Unable to create user account")
                    Toast.makeText(this@RegisterActivity, "Account creation failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // saving email and password to the preferences
    private fun saveToPrefs(email: String, password: String) {
        val prefs = applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)
        prefs.edit().putString("USER_EMAIL", email).apply()
        prefs.edit().putString("USER_PASSWORD", password).apply()
    }

    // going to the Login Activity - Login Page
    private fun goToLogin() {
        val registerIntent = Intent(this, LoginActivity::class.java)
        startActivity(registerIntent)
    }
}