package com.pp.a4rent.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pp.a4rent.adapters.BlogAdapter
import com.pp.a4rent.databinding.ActivityBlogListBinding
import com.pp.a4rent.models.Blog
import android.util.Log

class BlogListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val blogs = listOf(
            Blog("4 simple and affordable ways to stay healthy this fall", "No one wants the discomfort – let alone the hassle – that comes with catching a cold or other illness. But you don’t have to shell out for expensive remedies or shots of ginger or kale smoothies every day to support your immune system or stay healthy. Here are a few affordable steps you can take to safeguard your health.", "blog1"),
            Blog("4 ways to minimize financial stress", "For many Canadians, money worries are a source of stress. And the higher cost of borrowing we are experiencing can make things worse for those who are struggling to pay off their debts.", "blog2"),
            Blog("The ultimate green Halloween hacks", "What colour comes to mind when you think of Halloween? If it’s a bold shade of orange, it’s time to start thinking green.\n" +
                    "\n" +
                    "But don’t get spooked out by being an environmental champion, turn your Halloween into Hallow-green with these simple tips that will help protect our environment, one trick (or treat) at a time.", "blog3")
        )

        val adapter = BlogAdapter(blogs.toMutableList()) { position ->
            val intent = Intent(this, BlogDetailActivity::class.java)
            intent.putExtra("blog", blogs[position])
            startActivity(intent)
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}
