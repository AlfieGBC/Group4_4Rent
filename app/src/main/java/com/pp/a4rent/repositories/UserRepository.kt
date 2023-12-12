package com.pp.a4rent.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.pp.a4rent.models.User
import java.lang.Exception

class UserRepository(private val context: Context) {
    private val TAG = this.toString()
    private val db = Firebase.firestore

    private val COLLECTION_USERS = "Users"
    private val FIELD_FIRST_NAME = "firstName"
    private val FIELD_LAST_NAME = "lastName"
    private val FIELD_EMAIL = "email"
    private val FIELD_PASSWORD = "password"
    private val FIELD_PHONE = "phoneNumber"
    private val FIELD_ROLE = "role"
    private var loggedInUserEmail = ""

    var currentUser : MutableLiveData<User> = MutableLiveData<User>()

    fun addUserToDB(newUser: User) {
        Log.d(TAG, "addUserToDB called ${newUser}")
        try {
            val data: MutableMap<String, Any> = HashMap()

            data[FIELD_FIRST_NAME] = newUser.firstName
            data[FIELD_LAST_NAME] = newUser.lastName
            data[FIELD_EMAIL] = newUser.email
            data[FIELD_PASSWORD] = newUser.password
            data[FIELD_PHONE] = newUser.phoneNumber
            data[FIELD_ROLE] = newUser.role

            db.collection(COLLECTION_USERS)
                .document(newUser.email)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "addUserToDB: User document successfully created with ID $docRef")
                }.addOnFailureListener { ex ->
                    Log.e(TAG, "addUserToDB: Unable to create user document due to exception: $ex")
                }

        } catch (ex : Exception) {
            Log.e(TAG, "addUserToDB: Couldn't add user document : $ex")
        }
    }

    fun updateUserProfile(userToUpdate : User){
        try{
            val data : MutableMap<String, Any> = HashMap()

            data[FIELD_FIRST_NAME] = userToUpdate.firstName
            data[FIELD_LAST_NAME] = userToUpdate.lastName
            data[FIELD_EMAIL] = userToUpdate.email
            data[FIELD_PASSWORD] = userToUpdate.password
            data[FIELD_PHONE] = userToUpdate.phoneNumber

            Log.d(TAG, "data $data")

            db.collection(COLLECTION_USERS)
                .document(userToUpdate.email)
                .update(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "updateUserProfile: User document successfully updated $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "updateUserProfile: Unable to update user document due to exception : $ex", )
                }

        }catch (ex : Exception){
            Log.e(TAG, "updateUserProfile: Couldn't update user document $ex", )
        }
    }

    fun getUser(email: String) {
        try {
            db.collection(COLLECTION_USERS).document(email).get()
                .addOnSuccessListener {
                    Log.d(TAG, "getUser: ${it.toObject(User::class.java)}")
                    currentUser.postValue(it.toObject(User::class.java))
                }
        } catch (ex : Exception) {
            Log.e(TAG, "getUser: $ex", )
        }
    }
}