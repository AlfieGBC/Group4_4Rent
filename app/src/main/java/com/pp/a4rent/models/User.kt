package com.pp.a4rent.models

import java.util.UUID

data class User(
    var userId: String = UUID.randomUUID().toString(),
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var phoneNumber: String,
    var role: String,
    var favList: MutableList<PropertyRental>? = null,
    var propertyList: MutableList<PropertyRental>? = null
) {
}