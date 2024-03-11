package edu.uw.ischool.xyou.foodhub.data

data class Post (
    val id: String,
    val username: String,
    val title: String,
    val calories: Int,
    val descr: String,
    val date: String,
    val recipeIds: List<String>,
    val likes: List<String>
)