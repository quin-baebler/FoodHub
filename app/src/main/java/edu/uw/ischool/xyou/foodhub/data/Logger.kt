package edu.uw.ischool.xyou.foodhub.data

import org.json.JSONObject

data class Logger (
    val username: String,
    val date: String,
    val meals: ArrayList<Meal>,
    val calPerMeal: HashMap<String, Int>,
    val totalCal: Int
)

data class Meal (
    val name: String,
    val foods: ArrayList<FoodItem>,
    val totalCal: Int
)

data class FoodItem (
    val name: String,
    val foodId: String,
    val calories: Int,
    val serving: String,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)