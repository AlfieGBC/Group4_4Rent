package com.pp.a4rent.listeners

import com.pp.a4rent.models.Property

interface OnRentalPostClickListener {

    fun onRentalPropertySelected (property: Property)

    fun favButtonClicked(favRentals: Property)
}