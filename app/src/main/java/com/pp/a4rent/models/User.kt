package com.pp.a4rent.models

import java.io.Serializable

class User(
    var userId: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var phoneNumber: String,
    var role: String
) : Serializable {
    override fun toString(): String {
        return "User(userId='$userId', role='$role', " +
                "firstName='$firstName', lastName='$lastName'\n" +
                "email='$email'\n" +
                "phoneNumber='$phoneNumber'\n" +
                "password='$password'\n)"
    }
}