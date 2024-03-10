package edu.uw.ischool.xyou.foodhub.data

data class Logger (
    val date: String,
    val foodItems: HashMap<String, Pair<String, String>>,
    val totalCal: Int,
    val calPerMeal: HashMap<String, String>,
    val nutrition: List<String>
)
