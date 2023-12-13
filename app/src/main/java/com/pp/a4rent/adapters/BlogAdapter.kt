package com.pp.a4rent.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pp.a4rent.databinding.RowBlogItemBinding
import com.pp.a4rent.models.Blog
import com.pp.a4rent.listeners.OnBlogClickListener

class BlogAdapter(
    private val context: Context,
    private var blogsList: ArrayList<Blog>,
    private val readMoreBtnClickListener: OnBlogClickListener,
) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        return BlogViewHolder( RowBlogItemBinding.inflate( LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogsList[position], readMoreBtnClickListener)
    }

    override fun getItemCount(): Int {
        return blogsList.size
    }

    class BlogViewHolder(var binding: RowBlogItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(blog: Blog, readMoreBtnClickListener: OnBlogClickListener) {

            binding.tvTitle.setText(blog.title.toString())
            binding.tvDetail.setText(blog.description.toString())

            val imageName = blog.image
            val imageResId = itemView.context.resources.getIdentifier(
                imageName,
                "drawable",
                itemView.context.packageName
            )
            binding.image.setImageResource(imageResId)

            binding.readMoreButton.setOnClickListener {
                readMoreBtnClickListener.onBlogSelected(blog)
            }
        }
    }
}
