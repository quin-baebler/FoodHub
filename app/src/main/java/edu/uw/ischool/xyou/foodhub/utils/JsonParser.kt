package edu.uw.ischool.xyou.foodhub.utils

import edu.uw.ischool.xyou.foodhub.data.Post
import org.json.JSONArray

class JsonParser {
    fun parsePosts(jsonString: String): List<Post> {
        val posts = mutableListOf<Post>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val post = jsonArray.getJSONObject(i)
            val id = post.getString("id")
            val username = post.getString("username")
            val title = post.getString("title")
            val descr = post.getString("descr")
            val date = post.getString("date")
            val recipeIds = post.getJSONArray("recipeIds")
            val likes = post.getJSONArray("likes")
            val recipeIdsList = mutableListOf<String>()
            val likesList = mutableListOf<String>()
            for (j in 0 until recipeIds.length()) {
                recipeIdsList.add(recipeIds.getString(j))
            }
            for (j in 0 until likes.length()) {
                likesList.add(likes.getString(j))
            }
            posts.add(Post(id, username, title, descr, date, recipeIdsList, likesList))
        }
        return posts
    }
}