package com.pp.a4rent.models

import java.io.Serializable
import java.util.UUID

data class Blog(
    var blogID: String = UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    var image: String = ""
) : Serializable {

}
