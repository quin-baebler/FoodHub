package edu.uw.ischool.xyou.foodhub.utils

import android.util.Log
import edu.uw.ischool.xyou.foodhub.data.Logger
import edu.uw.ischool.xyou.foodhub.data.Comment
import edu.uw.ischool.xyou.foodhub.data.FoodItem
import edu.uw.ischool.xyou.foodhub.data.Meal
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
            val calories = post.getInt("calories")
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
            posts.add(Post(id, username, title, calories, descr, date, recipeIdsList, likesList))
        }
        return posts
    }

    fun parseLogger(jsonString: String): Logger {
        val item = JSONObject(jsonString)
        val username = item.getString("username")
        val totalCal = item.getInt("totalCal")
        val date = item.getString("date")

        val meals = item.getJSONArray("meals")
        val mealsList = arrayListOf<Meal>()
        val calPerMeal = hashMapOf<String, Int>()

        for(i in 0 until meals.length()){
            val meal = meals.getJSONObject(i)
            val mealName = meal.getString("name")
            val mealCal = meal.getInt("totalCal")

            val foods = meal.getJSONArray("foods")
            val foodsList = arrayListOf<FoodItem>()
            for(j in 0 until foods.length()){
                val food = foods.getJSONObject(j)
                foodsList.add(parseFood(food))
            }

            calPerMeal[mealName] = mealCal

            mealsList.add(Meal(mealName, foodsList, mealCal))
        }

        return Logger(username, date, mealsList, calPerMeal, totalCal)
    }

    fun parseFood(food: JSONObject): FoodItem {
        val name = food.getString("name")
        val calories = food.getInt("calories")
        val serving = food.getString("serving")
        val protein = food.getDouble("protein")
        val carbs = food.getDouble("carbs")
        val fat = food.getDouble("fat")

        return FoodItem(name, calories, serving, protein, carbs, fat)
    }

    fun parseSearchFood(jsonString: String): List<FoodItem> {
        val resList = arrayListOf<FoodItem>()
        val obj = JSONObject(jsonString)
        val jsonArray = obj.getJSONArray("info")
        for (i in 0 until jsonArray.length()) {
            val food = jsonArray.getJSONObject(i)
            val name = food.getString("food_name")
            val desc = food.getString("food_description")
            resList.add(parseDesc(name, desc))
        }
        return resList
    }

    fun parseDesc(name: String, desc: String): FoodItem {
        var serving = "N/A"
        var calories = 0
        var fat = 0.0
        var carbs = 0.0
        var protein = 0.0
        val pattern = "(Per \\d+g) - Calories: (\\d+)kcal .*?Fat: ([\\d.]+)g .*?Carbs: ([\\d.]+)g .*?Protein: ([\\d.]+)g".toRegex()
        val matchResult = pattern.find(desc)

        Log.i("DATA", "data: ${matchResult?.groupValues?.get(4)}")

        if (matchResult != null) {
            serving = matchResult.groupValues[1]
            calories = matchResult.groupValues[2].toInt()
            fat = matchResult.groupValues[3].toDouble()
            carbs = matchResult.groupValues[4].toDouble()
            protein = matchResult.groupValues[5].toDouble()
        }

        return FoodItem(name, calories, serving, protein, carbs, fat)
    }

    fun parseComments(jsonString: String): List<Comment> {
        val comments = mutableListOf<Comment>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val commentObj = jsonArray.getJSONObject(i)
            val commentId = commentObj.getString("id")
            val username = commentObj.getString("username")
            val comment = commentObj.getString("comment")
            val date = commentObj.getString("date")
            comments.add(Comment(commentId, username, comment, date))
        }
        return comments
    }
}