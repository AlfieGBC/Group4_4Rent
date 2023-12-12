package com.pp.a4rent.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pp.a4rent.adapters.BlogAdapter
import com.pp.a4rent.databinding.ActivityBlogListBinding
import com.pp.a4rent.models.Blog
import com.pp.a4rent.repositories.BlogRepository
import com.pp.a4rent.clickListeners.OnBlogClickListener

class BlogListActivity : AppCompatActivity(), OnBlogClickListener {
    private lateinit var binding: ActivityBlogListBinding

    private lateinit var blogRepository: BlogRepository
    private lateinit var blogAdapter: BlogAdapter
    private lateinit var blogArrayList: ArrayList<Blog>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blogArrayList = ArrayList()
        blogAdapter = BlogAdapter(this, blogArrayList, this)
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = blogAdapter

        blogRepository = BlogRepository(applicationContext)

    }

    override fun onResume() {
        super.onResume()

        blogRepository.retrieveAllBlogs()

        blogRepository.allBlogs.observe(this, androidx.lifecycle.Observer { blogsList ->

            if(blogsList != null){
//                clear the existing list to avoid duplicate records
                blogArrayList.clear()
                blogArrayList.addAll(blogsList)
                blogAdapter.notifyDataSetChanged()

            }
        })


    }

    override fun onBlogSelected(blog: Blog) {

        val mainIntent = Intent(this, BlogDetailActivity::class.java)
        mainIntent.putExtra("CURRENT_BLOG", blog)
        startActivity(mainIntent)
    }

}
