package edu.uw.ischool.xyou.foodhub.data

data class Logger (
    val id: String,
    val username: String,
    val date: String,
    val foodIds: List<String>,
    val totalCal: Int,
    val calPerMeal: HashMap<String, Int>
)
