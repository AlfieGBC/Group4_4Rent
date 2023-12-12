package com.pp.a4rent.clickListeners

import com.pp.a4rent.models.Blog

interface OnBlogClickListener {
    fun onBlogSelected(blog: Blog)
}