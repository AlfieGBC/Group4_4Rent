package com.pp.a4rent.models

class PropertyRental(
    var propertyType: String,
//    var ownerName: Owner,
//    var ownerEmail: Owner,
//    var ownerPhoneNumber: Owner,
    var ownerInfo: Owner,
    var NumberOfBedrooms:Int,
    var NumberOKitchen: Int,
    var NumberOfBathroom: Int,
    var description: String,
    var propertyAddress: String,
    var rent: Double,
    var available: Boolean,
    var imageFilename:String
    ) {

//    Property type (condo, house, apartment, basement, etc.).
//    • Owner name,
//    • Owner contact detail, (email and phone number)
//    • Property specifications such as Number of bedrooms, kitchen, bathroom, etc.
//    • Description
//    • Property address
//    • If available for rent

    override fun toString(): String {
        return "PropertyRental: " +
                "propertyType='$propertyType\n" +
                "ownerName=$ownerInfo\n" +
                "NumberOfBedrooms=$NumberOfBedrooms\n" +
                "NumberOKitchen=$NumberOKitchen\n" +
                "NumberOfBathroom=$NumberOfBathroom\n" +
                "Description=$description\n" +
                "PropertyAddress=$propertyAddress\n" +
                "Rent=$rent\n" +
                "Available=$available\n" +
                "ImageFileName=$imageFilename"
    }
}