package com.pp.a4rent.models

import java.io.Serializable

class Address(
    var street: String = "",
    var city: String = "",
    var province: String = "",
    var country: String = "",
) : Serializable
