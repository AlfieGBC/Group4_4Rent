package com.pp.a4rent.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pp.a4rent.databinding.RowRentalPostBinding
import com.pp.a4rent.listeners.OnRentalPostClickListener
import com.pp.a4rent.models.Property

class RentalsPostAdapter(
    private val context: Context,
    private val rentalsList: ArrayList<Property>,
    private val onRentalPostClickListener: OnRentalPostClickListener,
    private val favBtnClickListener: OnRentalPostClickListener,
    private var favList: List<Property>
) : RecyclerView.Adapter<RentalsPostAdapter.RentalsPostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalsPostViewHolder {
        return RentalsPostViewHolder( RowRentalPostBinding.inflate( LayoutInflater.from(context), parent, false))

    }

    override fun onBindViewHolder(holder: RentalsPostAdapter.RentalsPostViewHolder, position: Int) {
        holder.bind(rentalsList[position], onRentalPostClickListener, favBtnClickListener, favList)
    }

    override fun getItemCount(): Int {
        return rentalsList.size
    }

    class RentalsPostViewHolder(var binding: RowRentalPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property, onRentalPostClickListener: OnRentalPostClickListener, favBtnClickListener: OnRentalPostClickListener, favList: List<Property>) {

            // Populate the UI with the rental post details
            binding.tvTitle.setText("$${property.rent}")

            // No. of bedrooms and bathrooms
            binding.tvBeds.setText("${property.numberOfBedroom} Beds |")
            binding.tvBaths.setText("${property.numberOfBathroom} Baths |")

            // Area
            binding.tvArea.setText("${property.area} sq. ft.")

            // Populate the tv Property type
            binding.tvDetail.setText("${property.propertyType}")

            // address
            binding.tvAddress.setText("${property.propertyAddress.street}, ${property.propertyAddress.city}, \n" +
                    "${property.propertyAddress.province}, ${property.propertyAddress.country}")

            // city
            binding.tvCity.setText("${property.propertyAddress.city}")

            // For image
            val imageName = property.imageFilename
            val imageResId = itemView.context.resources.getIdentifier(
                imageName,
                "drawable",
                itemView.context.packageName
            )
            binding.ivRentalPostImage.setImageResource(imageResId)

            // For favourite btn
            binding.btnFavorite.setOnClickListener {
                favBtnClickListener.favButtonClicked(property)
            }

            val isFavorite = favList.any {
                it.propertyId == property.propertyId
            }

            binding.btnFavorite.setChecked(isFavorite)

            // for row clicked
            itemView.setOnClickListener {
                onRentalPostClickListener.onRentalPropertySelected(property)
            }
        }
    }

}