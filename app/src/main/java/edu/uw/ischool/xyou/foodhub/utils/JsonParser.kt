package edu.uw.ischool.xyou.foodhub.utils

import edu.uw.ischool.xyou.foodhub.data.Food
import edu.uw.ischool.xyou.foodhub.data.Logger
import edu.uw.ischool.xyou.foodhub.data.Post
import org.json.JSONArray
import org.json.JSONObject

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

    fun parseLogger(jsonString: String): Logger {
        val item = JSONObject(jsonString)
        val date = item.getString("date")
        val totalCal = item.getInt("totalCal")

        val calPerMeal = item.getJSONObject("calPerMeal")
        val breakfast = calPerMeal.getString("breakfast")
        val lunch = calPerMeal.getString("lunch")
        val snack = calPerMeal.getString("snack")
        val dinner = calPerMeal.getString("dinner")
        val perMeal = HashMap<String, String>()
        perMeal["breakfast"] = breakfast
        perMeal["lunch"] = lunch
        perMeal["snack"] = snack
        perMeal["dinner"] = dinner

        val foods = item.getJSONArray("foodItems")
        val foodDetails = HashMap<String, Pair<String, String>>()
        val nutrition = ArrayList<String>(3)

        for (j in 0 until foods.length()) {
            val foodName = foods.getJSONObject(j).keys()
            while (foodName.hasNext()) {
                val key = foodName.next()
                val food = foods.getJSONObject(j).getJSONObject(key)

                nutrition[0] = food.getString("protein")
                nutrition[1] = food.getString("carbs")
                nutrition[2] = food.getString("fat")

                val servings = food.getJSONArray("servings")
                foodDetails[key] = Pair(servings.getString(0), servings.getInt(1).toString())
            }
        }

        return Logger(date, foodDetails, totalCal, perMeal, nutrition)
    }

    fun parseFood(jsonString: String): List<Food> {
        val foods = mutableListOf<Food>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val desc = item.getString("food_description")
            val name = item.getString("food_name")
            val type = item.getString("food_type")
            foods.add(Food(name, desc, type))
        }
        return foods
    }
}