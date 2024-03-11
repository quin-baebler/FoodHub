package edu.uw.ischool.xyou.foodhub.data

import org.json.JSONObject

data class Logger (
    val date: String,
    val totalCal: Int,
    val mealInfo: List<Meal>,
    val totalNutrition: List<Int>
)

data class Meal (
    val mealName: String,
    val mealCal: String,
    val food: List<FoodItem>,
    val nutrition: List<Int>
)

data class FoodItem (
    val name: String,
    val calorie: String,
    val serving: String,
    val nutrition: List<Int>
)
