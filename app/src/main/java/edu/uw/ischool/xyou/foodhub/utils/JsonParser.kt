package edu.uw.ischool.xyou.foodhub.utils

import android.util.Log
import edu.uw.ischool.xyou.foodhub.data.Food
import edu.uw.ischool.xyou.foodhub.data.FoodItem
import edu.uw.ischool.xyou.foodhub.data.Logger
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
        //array of nutrition value [protein, carbs, fat]
        val overallNutrition = arrayListOf<Int>(0,0,0)
        val mealList = arrayListOf<Meal>()
        val meals = arrayListOf<String>("breakfast", "lunch", "snack", "dinner")

        val item = JSONObject(jsonString)
        val date = item.getString("date")
        val totalCal = item.getInt("totalCal")

        val foods = item.getJSONArray("foodItems")

        val calPerMeal = item.getJSONObject("calPerMeal")
        for(i in meals.indices) {
            val foodList = arrayListOf<FoodItem>()
            val nutritionPerMeal = arrayListOf<Int>(0, 0, 0)

            val chosenMeal = calPerMeal.getJSONObject(meals[i])
            val cal = chosenMeal.getString("total")
            val mealFoods = chosenMeal.getJSONArray("foods")

            //get food info for each food item in a meal
            for (j in 0 until mealFoods.length()) {
                val nutritionPerItem = arrayListOf<Int>(0, 0, 0)
                val foodItem = mealFoods.getString(j)

                for(k in 0 until foods.length()) {
                    val foodObj = foods.getJSONObject(k).keys()

                    while(foodObj.hasNext()) {
                        val key = foodObj.next()
                        if(key == foodItem) {
                            val food = foods.getJSONObject(k).getJSONObject(key)
                            val servings = food.getJSONArray("serving")
                            val servingSize = servings.getString(0)
                            val calPerServing = servings.getInt(1).toString()

                            nutritionPerItem[0] = food.getInt("protein")
                            nutritionPerItem[1] = food.getInt("carbs")
                            nutritionPerItem[2] = food.getInt("fat")

                            nutritionPerMeal[0] += food.getInt("protein")
                            nutritionPerMeal[1] += food.getInt("carbs")
                            nutritionPerMeal[2] += food.getInt("fat")

                            overallNutrition[0] += food.getInt("protein")
                            overallNutrition[1] += food.getInt("carbs")
                            overallNutrition[2] += food.getInt("fat")

                            foodList.add(FoodItem(mealFoods.getString(j), calPerServing, servingSize, nutritionPerItem))
                        }
                    }
                }
            }

            mealList.add(Meal(meals[i], cal, foodList, nutritionPerMeal))
        }

        return Logger(date, totalCal, mealList, overallNutrition)
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