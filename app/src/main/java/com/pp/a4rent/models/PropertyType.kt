package com.pp.a4rent.models

enum class PropertyType(val displayName: String) {
    CONDO("Condo"),
    HOUSE("House"),
    APARTMENT("Apartment"),
    BASEMENT("Basement");

    companion object {
        val displayNames = values().map { it.displayName }
    }
}
