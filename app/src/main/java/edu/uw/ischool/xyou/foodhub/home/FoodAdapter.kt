package edu.uw.ischool.xyou.foodhub.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.xyou.foodhub.data.FoodItem
import edu.uw.ischool.xyou.foodhub.databinding.FoodItemBinding

class FoodAdapter (
    private val foods: List<FoodItem>
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(val binding: FoodItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = FoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.binding.foodItemTitle.text = food.name
        holder.binding.foodItemCal.text = "Calories: ${food.calories}"
        holder.binding.foodItemServingSize.text = "Serving: ${food.serving}"
        holder.binding.foodItemProtein.text = "Protein: ${food.protein}"
        holder.binding.foodItemCarbs.text = "Carbs: ${food.carbs}"
        holder.binding.foodItemFat.text = "Fat: ${food.fat}"
    }

    override fun getItemCount(): Int {
        return foods.size
    }
}