package com.pp.a4rent.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pp.a4rent.adapters.BlogAdapter
import com.pp.a4rent.databinding.ActivityBlogDetailBinding
import com.pp.a4rent.models.Blog

class BlogDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val blog = intent.getSerializableExtra("blog") as Blog

        binding.title.text = blog.title
        binding.description.text = blog.description
        val imageName = blog.image
        val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
        binding.image.setImageResource(imageResId)

    }
}

