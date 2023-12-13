package com.pp.a4rent.models

import java.io.Serializable

data class Geo(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
) : Serializable