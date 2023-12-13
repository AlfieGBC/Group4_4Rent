package com.pp.a4rent.listeners

import com.pp.a4rent.models.Blog

interface OnBlogClickListener {
    fun onBlogSelected(blog: Blog)
}