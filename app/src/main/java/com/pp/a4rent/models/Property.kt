package com.pp.a4rent.models

import java.io.Serializable
import java.util.UUID

data class Property(
//    var propertyId: String = UUID.randomUUID().toString(),
    var propertyType: PropertyType,
    var ownerInfo: String,
    var numberOfBedroom:Int,
    var numberOKitchen: Int,
    var numberOfBathroom: Int,
    var area: Double,
    var description: String,
    var propertyAddress: Address,
    var rent: Double,
    var available: Boolean,
    var geo: Geo,
    var imageFilename: String? = null,
) : Serializable {
    override fun toString(): String {
        return "Property(propertyType=$propertyType, ownerInfo='$ownerInfo', numberOfBedroom=$numberOfBedroom, numberOKitchen=$numberOKitchen, numberOfBathroom=$numberOfBathroom, area=$area, description='$description', propertyAddress=$propertyAddress, rent=$rent, available=$available, geo=$geo, imageFilename=$imageFilename)"
    }
}