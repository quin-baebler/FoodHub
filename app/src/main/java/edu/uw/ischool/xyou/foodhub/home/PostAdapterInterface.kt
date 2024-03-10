package edu.uw.ischool.xyou.foodhub.home

import edu.uw.ischool.xyou.foodhub.data.Post

interface PostAdapterInterface {
    fun onItemClicked(post: Post)
}