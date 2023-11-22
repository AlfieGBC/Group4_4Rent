package com.pp.a4rent.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pp.a4rent.R
import com.pp.a4rent.models.PropertyRental

class MyListingsAdapter(var myListings: List<PropertyRental>) : RecyclerView.Adapter<MyListingsAdapter.MyListingsViewHolder>() {

    inner class MyListingsViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListingsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_my_listings_adapter, parent, false)
        return MyListingsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myListings.size
    }

    override fun onBindViewHolder(holder: MyListingsViewHolder, position: Int) {
//        val tvLabel = holder.itemView.findViewById<TextView>(R.id.tvRowLine1)
//        tvLabel.text = "Task: ${myListings[position]}"
    }

}
