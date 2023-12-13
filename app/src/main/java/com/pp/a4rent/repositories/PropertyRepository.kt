package com.pp.a4rent.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.pp.a4rent.models.Property

class PropertyRepository(private val context : Context) {
    private val TAG = "check"
    private val db = Firebase.firestore
    private val sharedPrefs : SharedPreferences = context.getSharedPreferences("com.pp.a4rent", Context.MODE_PRIVATE)
    private var loggedInUserEmail = ""

    private val COLLECTION_PROPERTIES = "Property"
    private val COLLECTION_FAV_LIST = "Property";
    private val COLLECTION_PROPERTY_LIST = "Property"
    private val COLLECTION_USERS = "Users";

    private val FIELD_propertyType = "propertyType";
    private val FIELD_ownerInfo = "ownerInfo"
    private val FIELD_numberOfBedroom = "numberOfBedroom"
    private val FIELD_numberOKitchen = "numberOKitchen";
    private val FIELD_numberOfBathroom = "numberOfBathroom";
    private val FIELD_area = "area"
    private val FIELD_description = "description";
    private val FIELD_propertyAddress = "propertyAddress";
    private val FIELD_rent = "rent"
    private val FIELD_available = "available";
    private val FIELD_geo = "geo";
    private val FIELD_imageFilename = "imageFilename";
    private val FIELD_favourite = "favourite"
    private val FIELD_ID = "id"

    val property: MutableLiveData<Property> = MutableLiveData<Property>()
    var allProperties : MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()
    var allPropertiesInFavList: MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()
    var allPropertiesInPropertyList: MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()

    init {
        if (sharedPrefs.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
        }
    }


    // fun to retrieve the user roles from the database
     fun getUserRoleFromDatabase(email: String, callback: (String) -> Unit) {
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

    fun getSinglePropertyById(propertyId: String){
        try {
            db.collection(COLLECTION_PROPERTIES)
                .document(propertyId)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "getSinglePropertyById: Successfully get User: $it")
                    val propertyObj = it.toObject(Property::class.java)
                    Log.d(TAG, "getSinglePropertyById: propertyObj: $propertyObj")

                    property.postValue(propertyObj)
                }
        }catch (ex: Exception){
            Log.e(TAG, "getSinglePropertyById: Failed to get the property object by id: $propertyId", ex)
        }
    }


    fun addProperty(newProperty: Property){
        try {
            val data: MutableMap<String, Any> = HashMap()

            data[FIELD_ID] = newProperty.propertyId
            data[FIELD_propertyType] = newProperty.propertyType

            data[FIELD_ownerInfo] = newProperty.ownerInfo
            data[FIELD_numberOfBedroom] = newProperty.numberOfBedroom
            data[FIELD_numberOKitchen] = newProperty.numberOKitchen
            data[FIELD_numberOfBathroom] = newProperty.numberOfBathroom
            data[FIELD_area] = newProperty.area
            data[FIELD_description] = newProperty.description
            data[FIELD_propertyAddress] = newProperty.propertyAddress
            data[FIELD_rent] = newProperty.rent
            data[FIELD_available] = newProperty.available
            data[FIELD_geo] = newProperty.geo
            data[FIELD_imageFilename] = newProperty.imageFilename?:""

            db.collection(COLLECTION_PROPERTIES)
                .add(data)
                .addOnSuccessListener {
                    Log.d(TAG, "addProperty: Successfully added User: ${newProperty.toString()}.")
                }
                .addOnFailureListener {
                    Log.e(TAG, "addProperty: Failed to add User: ${newProperty.toString()} due to exception: $it", )
                }

        }catch (ex : Exception){
            Log.e(TAG, "addProperty: Couldn't perform insert on Property collection due to exception : $ex", )
        }
    }


    fun addPropertyToPropertyList(newProperty: Property){
        if (loggedInUserEmail.isNotEmpty()){
            try {
                val data: MutableMap<String, Any> = HashMap()

                data[FIELD_ID] = newProperty.propertyId
                data[FIELD_propertyType] = newProperty.propertyType
                data[FIELD_ownerInfo] = newProperty.ownerInfo
                data[FIELD_numberOfBedroom] = newProperty.numberOfBedroom
                data[FIELD_numberOKitchen] = newProperty.numberOKitchen
                data[FIELD_numberOfBathroom] = newProperty.numberOfBathroom
                data[FIELD_area] = newProperty.area
                data[FIELD_description] = newProperty.description
                data[FIELD_propertyAddress] = newProperty.propertyAddress
                data[FIELD_rent] = newProperty.rent
                data[FIELD_available] = newProperty.available
                data[FIELD_geo] = newProperty.geo
                data[FIELD_imageFilename] = newProperty.imageFilename?:""

                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_PROPERTY_LIST)
                    .add(data)
                    .addOnSuccessListener {
                        Log.d(TAG, "addPropertyToPropertyList: Successfully added User: ${newProperty.toString()}.")
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "addPropertyToPropertyList: Failed to add User: ${newProperty.toString()} due to exception: $it", )
                    }

            }catch (ex : Exception){
                Log.e(TAG, "addPropertyToPropertyList: Couldn't perform insert on Property sub-collection due to exception : $ex", )
            }
        }else{
            Log.e(TAG, "addPropertyToPropertyList: Cannot retrieve properties without user's email address. You must sign in first.", )
        }
    }


    fun getAllProperties(){
        try {
            db.collection(COLLECTION_PROPERTIES)
                .addSnapshotListener(EventListener{ result, error ->
                    if (error != null){
                        Log.e(TAG, "getAllProperties: Listening for property collection failed due to error", error)
                        return@EventListener
                    }

                    if (result != null){
                        Log.d(TAG, "getAllProperties: Number of documents received: ${result.size()}")
                        val tempList : MutableList<Property> = ArrayList<Property>()

                        for (docChanges in result.documentChanges){
                            val currProperty = docChanges.document.toObject(Property::class.java)
                            Log.d(TAG, "getAllProperties: currProperty: $currProperty")

                            when(docChanges.type){
                                DocumentChange.Type.ADDED -> {
                                    tempList.add(currProperty)
                                }

                                DocumentChange.Type.MODIFIED -> {}

                                DocumentChange.Type.REMOVED -> {
                                    tempList.remove(currProperty)
                                }
                            }
                        }
                        Log.d(TAG, "getAllProperties: tempList: $tempList")
                        allProperties.postValue(tempList)
                    }
                })
        }catch (ex: Exception){
            Log.d(TAG, "getAllProperties: Can't retrieve all the properties due to exception: $ex")
        }
    }


    fun getAllPropertiesFromPropertyList(){
        if (loggedInUserEmail.isNotEmpty()){
            try {
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_PROPERTY_LIST)
                    .addSnapshotListener(EventListener { result, error ->
                        if (error != null){
                            Log.e(TAG, "getAllPropertiesFromPropertyList: Listening for property sub-collection failed due to error", error)
                        }

                        if (result != null){
                            Log.d(TAG, "getAllPropertiesFromPropertyList: Number of documents received: ${result.size()}")
                            val tempList : MutableList<Property> = ArrayList<Property>()

                            for (docChanges in result.documentChanges){
                                val currProperty = docChanges.document.toObject(Property::class.java)
                                Log.d(TAG, "getAllPropertiesFromPropertyList: currProperty: $currProperty")

                                when(docChanges.type){
                                    DocumentChange.Type.ADDED -> {
                                        tempList.add(currProperty)
                                    }
                                    DocumentChange.Type.MODIFIED -> {}
                                    DocumentChange.Type.REMOVED -> {
                                        tempList.remove(currProperty)
                                    }
                                }
                            }
                            Log.d(TAG, "getAllPropertiesFromPropertyList: tempList: $tempList")
                            allPropertiesInPropertyList.postValue(tempList)
                        }
                    })
            }catch (ex: Exception){
                Log.e(TAG, "getAllPropertiesFromPropertyList: exception", ex)
            }
        }else{
            Log.e(TAG, "getAllPropertiesFromPropertyList: Cannot retrieve properties without user's email address. You must sign in first.", )
        }
    }


    fun updateProperty(propertyToUpdate: Property){
        val data: MutableMap<String, Any> = HashMap()
        Log.d(TAG, "updateProperty: propertyToUpdate: $propertyToUpdate")

        data[FIELD_propertyType] = propertyToUpdate.propertyType
        data[FIELD_ownerInfo] = propertyToUpdate.ownerInfo
        data[FIELD_numberOfBedroom] = propertyToUpdate.numberOfBedroom
        data[FIELD_numberOKitchen] = propertyToUpdate.numberOKitchen
        data[FIELD_numberOfBathroom] = propertyToUpdate.numberOfBathroom
        data[FIELD_area] = propertyToUpdate.area
        data[FIELD_description] = propertyToUpdate.description
        data[FIELD_propertyAddress] = propertyToUpdate.propertyAddress
        data[FIELD_rent] = propertyToUpdate.rent
        data[FIELD_available] = propertyToUpdate.available
        data[FIELD_geo] = propertyToUpdate.geo
        data[FIELD_imageFilename] = propertyToUpdate.imageFilename?:""

        try {
            db.collection(COLLECTION_PROPERTIES)
                .document(propertyToUpdate.propertyId)
                .update(data)
                .addOnSuccessListener {
                    Log.d(TAG, "updateProperty: Updated successfully!")
                }
                .addOnFailureListener {
                    Log.e(TAG, "updateProperty: Failed to update property: $propertyToUpdate", it)
                }
        }catch (ex: Exception){
            Log.e(TAG, "updateProperty: Failed to update property: $propertyToUpdate", ex)
        }
    }

    fun updatePropertyInPropertyList(propertyToUpdate: Property){
        val data: MutableMap<String, Any> = HashMap()
        Log.d(TAG, "updatePropertyInPropertyList: propertyToUpdate: $propertyToUpdate")


        data[FIELD_propertyType] = propertyToUpdate.propertyType
        data[FIELD_ownerInfo] = propertyToUpdate.ownerInfo
        data[FIELD_numberOfBedroom] = propertyToUpdate.numberOfBedroom
        data[FIELD_numberOKitchen] = propertyToUpdate.numberOKitchen
        data[FIELD_numberOfBathroom] = propertyToUpdate.numberOfBathroom
        data[FIELD_area] = propertyToUpdate.area
        data[FIELD_description] = propertyToUpdate.description
        data[FIELD_propertyAddress] = propertyToUpdate.propertyAddress
        data[FIELD_rent] = propertyToUpdate.rent
        data[FIELD_available] = propertyToUpdate.available
        data[FIELD_geo] = propertyToUpdate.geo
        data[FIELD_imageFilename] = propertyToUpdate.imageFilename?:""

        try {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_PROPERTIES)
                .document(propertyToUpdate.propertyId)
                .update(data)
                .addOnSuccessListener {
                    Log.d(TAG, "updatePropertyInPropertyList: Updated successfully!")
                }
                .addOnFailureListener {
                    Log.e(TAG, "updatePropertyInPropertyList: Failed to update property: $propertyToUpdate", it)
                }
        }catch (ex: Exception){
            Log.e(TAG, "updatePropertyInPropertyList: Failed to update property: $propertyToUpdate", ex)
        }
    }

    fun deleteProperty(propertyToDelete: Property){
        try {
            db.collection(COLLECTION_PROPERTIES)
                .document(propertyToDelete.propertyId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "deleteProperty: Property deleted successfully! Property: $propertyToDelete")
                }
                .addOnFailureListener {
                    Log.e(TAG, "deleteProperty: Failed to delete property: $propertyToDelete", it)
                }
        }catch (ex: Exception){
            Log.e(TAG, "deleteProperty: Failed to delete property: $propertyToDelete due to exception.", ex)
        }
    }

    fun deletePropertyFromPropertyList(){}


    fun filterPropertyFromPropertyList() {

    }

    // TODO : For Favourite List

    // For inserting data of rental property post to the db
    fun addPropertyToFavList(favRentalProperty: Property){
        if (loggedInUserEmail.isNotEmpty()) {
            try {
                val data: MutableMap<String, Any> = HashMap()

                data[FIELD_propertyType] = favRentalProperty.propertyType
                data[FIELD_ownerInfo] = favRentalProperty.ownerInfo
                data[FIELD_numberOfBedroom] = favRentalProperty.numberOfBedroom
                data[FIELD_numberOKitchen] = favRentalProperty.numberOKitchen
                data[FIELD_numberOfBathroom] = favRentalProperty.numberOfBathroom
                data[FIELD_area] = favRentalProperty.area
                data[FIELD_description] = favRentalProperty.description
                data[FIELD_propertyAddress] = favRentalProperty.propertyAddress
                data[FIELD_rent] = favRentalProperty.rent
                data[FIELD_available] = favRentalProperty.available
                data[FIELD_geo] = favRentalProperty.geo
                data[FIELD_imageFilename] = favRentalProperty.imageFilename?:""
                data[FIELD_favourite] = favRentalProperty.favourite?:true

                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_FAV_LIST)
                    .add(data)
                    .addOnSuccessListener {
                        Log.d(TAG, "addPropertyToFavList: Successfully added rental property to fav list: ${favRentalProperty.toString()}.")
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "addPropertyToFavList: Failed to add rental property to fav list: ${favRentalProperty.toString()} due to exception: $it", )
                    }

            } catch (ex : Exception){
                Log.e(TAG, "addPropertyToFavList: Couldn't perform insert on Property sub-collection due to exception : $ex", )
            }
        } else{
            Log.e(TAG, "addPropertyToFavList: Cannot retrieve properties without user's email address. You must sign in first.", )
        }

    }

    fun isPropertyExistInFavList(favRentalProperty: Property, callback: (Boolean) -> Unit) {
        try {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_FAV_LIST)
                .whereEqualTo(FIELD_propertyAddress, favRentalProperty.propertyAddress)
                .get()
                .addOnSuccessListener { documents ->
                    val exists = !documents.isEmpty
                    callback(exists)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to check for rental property existence due to exception: $exception")
                    callback(false)
                }
        } catch (ex: Exception) {
            Log.e(TAG, "Couldn't perform find on Property sub-collection due to exception : $ex")
            callback(false)
        }
    }

    // Retrieving data of rental property from the db (Fav list collections)
    fun getAllPropertiesFromFavList(){
        if (loggedInUserEmail.isNotEmpty()){
            try {
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_FAV_LIST)
                    .addSnapshotListener(EventListener { result, error ->
                        if (error != null){
                            Log.e(TAG, "getAllPropertiesFromFavList: Listening for property sub-collection failed due to error", error)
                        }

                        if (result != null){
                            Log.d(TAG, "getAllPropertiesFromFavList: Number of documents received: ${result.size()}")
                            val tempList : MutableList<Property> = ArrayList<Property>()

                            for (docChanges in result.documentChanges){
                                val currProperty = docChanges.document.toObject(Property::class.java)
                                Log.d(TAG, "getAllPropertiesFromFavList: currProperty: $currProperty")

//                                currProperty.propertyId = docChanges.document.reference.id

                                when(docChanges.type){
                                    DocumentChange.Type.ADDED -> {
                                        tempList.add(currProperty)
                                    }
                                    DocumentChange.Type.MODIFIED -> {}
                                    DocumentChange.Type.REMOVED -> {
                                        tempList.remove(currProperty)
                                    }
                                }
                            }
                            Log.d(TAG, "getAllPropertiesFromFavList: tempList: $tempList")
                            allPropertiesInFavList.postValue(tempList)
                        }
                    })
            }catch (ex: Exception){
                Log.e(TAG, "getAllPropertiesFromFavList: exception", ex)
            }
        }else{
            Log.e(TAG, "getAllPropertiesFromFavList: Cannot retrieve properties without user's email address. You must sign in first.", )
        }
    }

    // For removing rental property posts from the favourite list
    fun deletePropertyFromFavList(favRentalPropertyToDelete: Property){
        try {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_FAV_LIST)
                .document(favRentalPropertyToDelete.propertyId)
                .delete()
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "deletePropertyFromFavList: document deleted successfully : $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "deletePropertyFromFavList: Failed to delete document: $ex")
                }
        } catch (ex: Exception) {
            Log.e(TAG, "deletePropertyFromFavList: Unable to delete rental post from fav list due to exception : $ex")
        }

    }

    fun deleteAllPropertyFromFavList() {
        try {
            // Remove all documents from the "COLLECTION_FAV_LIST" for the logged-in user
            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_FAV_LIST)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "deleteAllPropertyFromFavList: Failed to delete properties", exception)
                }
        } catch (ex: Exception) {
            Log.e(TAG, "deleteAllPropertyFromFavList: Exception occurred: $ex")
        }
    }

}