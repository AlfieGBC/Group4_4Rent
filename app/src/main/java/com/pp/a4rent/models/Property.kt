package com.pp.a4rent.models

import java.io.Serializable

class Property(
    var propertyType: PropertyType,
    var ownerInfo: User,
    var numberOfBedroom:Int,
    var numberOKitchen: Int,
    var numberOfBathroom: Int,
    var area: Double,
    var description: String,
    var propertyAddress: String,
    var rent: Double,
    var available: Boolean,
) : Serializable {
    override fun toString(): String {
        return "Property(propertyType=$propertyType, ownerInfo=$ownerInfo, numberOfBedroom=$numberOfBedroom, numberOKitchen=$numberOKitchen, numberOfBathroom=$numberOfBathroom, area=$area, description='$description', propertyAddress='$propertyAddress', rent=$rent, available=$available')"
    }
}