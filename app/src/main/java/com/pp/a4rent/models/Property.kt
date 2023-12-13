package com.pp.a4rent.models

import java.io.Serializable
import java.util.UUID

data class Property(
    var propertyId: String = UUID.randomUUID().toString(),
    var propertyType: PropertyType = PropertyType.APARTMENT,
    var ownerInfo: String = "",
    var numberOfBedroom:Int = 0,
    var numberOKitchen: Int = 0,
    var numberOfBathroom: Int = 0,
    var area: Double = 0.0,
    var description: String = "",
    var propertyAddress: Address = Address("", "", "", ""),
    var rent: Double = 0.0,
    var available: Boolean = true,
    var geo: Geo = Geo(0.0, 0.0),
    var imageFilename: String? = "",
    var favourite: Boolean? = false,
) : Serializable {

    override fun toString(): String {
        return "Property(propertyId='$propertyId', propertyType=$propertyType, ownerInfo='$ownerInfo', numberOfBedroom=$numberOfBedroom, numberOKitchen=$numberOKitchen, numberOfBathroom=$numberOfBathroom, area=$area, description='$description', propertyAddress=$propertyAddress, rent=$rent, available=$available, geo=$geo, imageFilename=$imageFilename, favourite=$favourite)"
    }
}