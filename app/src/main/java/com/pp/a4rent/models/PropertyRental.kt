package com.pp.a4rent.models

import java.io.Serializable

//PropertyRental must be Serializable interface or protocol
class PropertyRental(
    var propertyType: String,
//    var ownerName: Owner,
//    var ownerEmail: Owner,
//    var ownerPhoneNumber: Owner,
    var ownerInfo: Owner,
    var numberOfBedroom:Int,
    var numberOKitchen: Int,
    var numberOfBathroom: Int,
    var area: Double,
    var description: String,
    var propertyAddress: String,
    var rent: Double,
    var available: Boolean,
    var imageFilename:String
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
                "Rent=$rent\n" +
                "Available=$available\n" +
                "ImageFileName=$imageFilename"
    }
}