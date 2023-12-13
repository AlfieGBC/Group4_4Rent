package com.pp.a4rent.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pp.a4rent.R
import com.pp.a4rent.models.Property

class MyListingsAdapter(
    private var myListings: List<Property>,
    val listingRowClickedHandler: (Int) -> Unit,
) : RecyclerView.Adapter<MyListingsAdapter.MyListingsViewHolder>() {

    inner class MyListingsViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {
            itemView.setOnClickListener {
                listingRowClickedHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListingsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_my_listings_adapter, parent, false)
        return MyListingsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myListings.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyListingsViewHolder, position: Int) {
        val currListing = myListings[position]

        val tvRent = holder.itemView.findViewById<TextView>(R.id.tv_rent)
        val tvNumOfRooms = holder.itemView.findViewById<TextView>(R.id.tv_num_of_rooms)
        val tvPropertyType = holder.itemView.findViewById<TextView>(R.id.tv_property_type)
        val tvAddress = holder.itemView.findViewById<TextView>(R.id.tv_address)

        tvRent.text = "$${currListing.rent}"
        tvNumOfRooms.text = "${currListing.numberOfBedroom} Beds | ${currListing.numberOfBathroom} Baths"
        tvPropertyType.text = currListing.propertyType.displayName
        tvAddress.text = currListing.propertyAddress.street
    }

}
