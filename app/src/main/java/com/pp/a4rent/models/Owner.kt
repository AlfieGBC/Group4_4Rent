package com.pp.a4rent.models

class Owner(
    var name:String,
    var email:String,
    var phoneNumber:Int
) {
    override fun toString(): String {
        return "Owner(name='$name', email='$email', phoneNumber=$phoneNumber)"
    }
}