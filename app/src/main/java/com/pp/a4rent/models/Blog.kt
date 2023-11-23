package com.pp.a4rent.models

import java.io.Serializable

class Blog(
    var title: String,
    var description: String,
    var image: String
) : Serializable {

    override fun toString(): String {
        return "Blog(title='$title', description='$description', image='$image')"
    }
}
