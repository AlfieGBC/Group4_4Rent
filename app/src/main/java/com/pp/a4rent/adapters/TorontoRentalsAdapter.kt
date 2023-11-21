package com.pp.a4rent.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pp.a4rent.R
import com.pp.a4rent.models.PropertyRental

class TorontoRentalsAdapter(
    private val rentalsList:MutableList<PropertyRental>,
    private val rowClickHandler: (Int) -> Unit,
    private val favBtnClickHandler: (Int) -> Unit
) : RecyclerView.Adapter<TorontoRentalsAdapter.TorontoRentalsViewHolder>() {

    inner class TorontoRentalsViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {
            itemView.setOnClickListener {
                rowClickHandler(adapterPosition)
            }
            itemView.findViewById<Button>(R.id.btnFavorite).setOnClickListener {
                favBtnClickHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorontoRentalsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_rental_post, parent, false)
        return TorontoRentalsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentalsList.size
    }

    override fun onBindViewHolder(holder: TorontoRentalsViewHolder, position: Int) {
        // Get the current rental post
        var currRentalPost:PropertyRental = rentalsList.get(position)

        // Populate the UI with the rental post details
        // Get the tvTitle
        val tvTitle = holder.itemView.findViewById<TextView>(R.id.tvTitle)
        tvTitle.setText("${currRentalPost.rent}\n")

        // Populate the tvDetail
        val tvDetail = holder.itemView.findViewById<TextView>(R.id.tvDetail)
        tvDetail.setText("${currRentalPost.propertyType}")

        // 2c. Populate the image
        // - getting a context variable
        val context = holder.itemView.context

        // - use the context to update the image
        val res = context.resources.getIdentifier(currRentalPost.imageFilename, "drawable", context.packageName)

        val rentalPost = holder.itemView.findViewById<ImageView>(R.id.rentalPost)
        rentalPost.setImageResource(res)
    }
}