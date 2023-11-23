package com.pp.a4rent.models

import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.pp.a4rent.R
import com.pp.a4rent.adapters.RentalsPostAdapter
import java.io.Serializable

//PropertyRental must be Serializable interface or protocol
class PropertyRental(
    var rentalID : Int,
    var propertyType: String,
    var ownerInfo: Owner,
    var numberOfBedroom:Int,
    var numberOKitchen: Int,
    var numberOfBathroom: Int,
    var area: Double,
    var description: String,
    var propertyAddress: String,
    var city: String,
    var postalCode: String,
    var rent: Double,
    var available: Boolean,
    var imageFilename:String,
    var favourite: Boolean
    ) : Serializable {

//    Property type (condo, house, apartment, basement, etc.).
//    • Owner name,
//    • Owner contact detail, (email and phone number)
//    • Property specifications such as Number of bedrooms, kitchen, bathroom, etc.
//    • Area
//    • Description
//    • Property address
//    • If available for rent

    override fun toString(): String {
        return "PropertyRental: " +
                "propertyType='$propertyType\n" +
                "ownerName=$ownerInfo\n" +
                "NumberOfBedrooms=$numberOfBedroom\n" +
                "NumberOKitchen=$numberOKitchen\n" +
                "NumberOfBathroom=$numberOfBathroom\n" +
                "Area=$area\n" +
                "Description=$description\n" +
                "PropertyAddress=$propertyAddress\n" +
                "City=$city\n" +
                "PostalCode=$postalCode\n" +
                "Rent=$rent\n" +
                "Available=$available\n" +
                "ImageFileName=$imageFilename\n" +
                "Favourite:$favourite\n"
    }

//    private var rentalDatasource:MutableList<PropertyRental> = mutableListOf<PropertyRental>(
//        PropertyRental(1,"condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "Humber Line 123 Street, NY","Toronto", "N2M 8C9", 2500.0, true, "peter",false),
//        PropertyRental(2, "condo", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","Bloor St , NY","North York", "S2M 3C9",2500.0, true, "amy",false ),
//        PropertyRental(3,"condo", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", " Line 123 Street, Victoria, Vancouver","vancouver", "B2D 5N6", 2700.0, true, "alex",false),
//        PropertyRental(4,"basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo", "12 vi Street, Van","Vancouver", "B2D 5N6",2300.0, true, "jane",false),
//        PropertyRental(5,"condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "123 st abc, winnipeg", "Winnipeg","N4M 8C9",2500.0, true, "peter",false),
//        PropertyRental(6,"condo", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","12 Keele Street, NY","North York", "N2M 8C9",2500.0, true, "amy",false),
//        PropertyRental(7, "condo", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", "16 victoria st, BC","vancouver", "C2M 8C9",2500.0, true, "alex",false),
//        PropertyRental(8,"basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo", "879 Islington av, Etobicoke", "toronto", "N2B 8C9",2500.0, true, "jane",false),
//        PropertyRental(9,"condo", Owner("Peter", "peter@gmail.com", 123), 12, 2,3, 220.0,"cool condo", "456 abc st, winnipeg","winnipeg", "N2W 6C6",2500.0, true, "peter",false),
//        PropertyRental(10,"condo", Owner("Amy", "amy@gmail.com", 123), 12, 2,3, 500.0,"cool condo","78 jane st, north york","north york", "N2M 5C6",2500.0, true, "amy",false),
//        PropertyRental(11,"condo", Owner("Alex", "alex@gmail.com", 123), 12, 2,3, 330.0,"cool condo", "45 dupont st, toronto","Toronto", "N2MH 4C9",2500.0, true, "alex",false),
//        PropertyRental(12,"basement", Owner("Jane", "jane@gmail.com", 123), 12, 2,3, 800.0,"cool condo","46 dufferin st, toronto", "toronto", "I2O 8C3",2500.0, true, "jane",false),
//    )


}