package com.pp.a4rent.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.pp.a4rent.models.Address
import com.pp.a4rent.models.Geo
import com.pp.a4rent.models.Property
import com.pp.a4rent.models.PropertyType
import com.pp.a4rent.models.User
import java.util.UUID

class PropertyRepository(private val context : Context) {
    private val TAG = this.toString();
    private val db = Firebase.firestore
    private lateinit var sharedPrefs : SharedPreferences
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

    var allProperties : MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()
    var allPropertiesInFavList: MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()
    var allPropertiesInPropertyList: MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()

    init {
        sharedPrefs = context.getSharedPreferences("com.pp.a4rent", Context.MODE_PRIVATE)
        if (sharedPrefs.contains("USER_EMAIL")){
            loggedInUserEmail = sharedPrefs.getString("USER_EMAIL", "NA").toString()
        }
    }

    fun addProperty(newProperty: Property){
        try {
            val data: MutableMap<String, Any> = HashMap()
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

    fun addPropertyToFavList(newProperty: Property){}

    fun addPropertyToPropertyList(newProperty: Property){
        if (loggedInUserEmail.isNotEmpty()){
            try {
                val data: MutableMap<String, Any> = HashMap()
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

    fun updateProperty(){}

    fun updatePropertyInFavList(){}

    fun updatePropertyInPropertyList(){}

    fun deleteProperty(){}

    fun deletePropertyFromFavList(){}

    fun deletePropertyFromPropertyList(){}
}