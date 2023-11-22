package com.pp.a4rent.models

import java.io.Serializable

class Owner(
    var name:String,
    var email:String,
    var phoneNumber:Int
) : Serializable {
    override fun toString(): String {
        return "Owner(name='$name', email='$email', phoneNumber=$phoneNumber)"
    }
}