package com.pp.a4rent.models

enum class PropertyType(
    val displayName: String = ""
) {
    CONDO("Condo"),
    HOUSE("House"),
    APARTMENT("Apartment"),
    BASEMENT("Basement");

    companion object {
        val displayNames = values().map { it.displayName }

        // The fromDisplayName method takes a string and returns the corresponding PropertyType enum value.
        fun fromDisplayName(displayName: String): PropertyType {
            return values().first { it.displayName == displayName }
        }
    }
}
