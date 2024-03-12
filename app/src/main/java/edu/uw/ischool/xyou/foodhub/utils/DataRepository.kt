package edu.uw.ischool.xyou.foodhub.utils

import edu.uw.ischool.xyou.foodhub.data.FoodItem

object DataRepository {
    private val foodItems = mutableListOf<FoodItem>()

    fun addData(foodItem: FoodItem) {
        foodItems.add(foodItem)
    }

    fun deleteData(foodItem: FoodItem) {
        foodItems.remove(foodItem)
    }

    fun clearData() {
        foodItems.clear()
    }

    fun getFoodItems(): List<FoodItem> {
        return foodItems
    }
}
