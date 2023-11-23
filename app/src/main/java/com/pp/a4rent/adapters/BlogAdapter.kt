package com.pp.a4rent.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView

import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.pp.a4rent.R
import com.pp.a4rent.models.Blog

import android.util.Log

class BlogAdapter(
    private val blogsList: MutableList<Blog>,
    private val rowClickHandler: (Int) -> Unit
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.readMoreButton).setOnClickListener {
                rowClickHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_blog_item, parent, false)
        return BlogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return blogsList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val tvTitle = holder.itemView.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = blogsList[position].title

        val tvDetail = holder.itemView.findViewById<TextView>(R.id.tvDetail)
        tvDetail.text = blogsList[position].description

        val imageView = holder.itemView.findViewById<ImageView>(R.id.image)
        val imageName = blogsList[position].image
        val imageResId = holder.itemView.context.resources.getIdentifier(imageName, "drawable", holder.itemView.context.packageName)
        imageView.setImageResource(imageResId)
    }
}
