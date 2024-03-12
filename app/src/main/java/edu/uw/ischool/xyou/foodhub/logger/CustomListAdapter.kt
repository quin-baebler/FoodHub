package edu.uw.ischool.xyou.foodhub.logger

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.MainActivity
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.FoodItem
import edu.uw.ischool.xyou.foodhub.data.Logger
import edu.uw.ischool.xyou.foodhub.post.PostFragment
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.json.JSONObject

class CustomListAdapter(
    context: Context,
    val activity: Activity,
    val lifecycleScope: LifecycleCoroutineScope,
    val itemList: List<FoodItem>,
    val isAddFood: Boolean,
    val isFromPostFragment: Boolean
) : ArrayAdapter<FoodItem>(context, R.layout.list_item_layout, itemList) {
    private val TAG = "CustomListAdapter"
    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api/"
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false)
        }

        val currentItem = itemList[position]
        val title = itemView?.findViewById<TextView>(R.id.food_name)
        title?.text = currentItem.name

        val cal = itemView?.findViewById<TextView>(R.id.food_cal)
        cal?.text = "Calories: ${currentItem.calories}"

        val serving = itemView?.findViewById<TextView>(R.id.food_serving)
        serving?.text = "Serving: ${currentItem.serving}"

        val protein = itemView?.findViewById<TextView>(R.id.food_protein)
        protein?.text = "Protein: ${currentItem.protein}"

        val carbs = itemView?.findViewById<TextView>(R.id.food_carbs)
        carbs?.text = "Carbs: ${currentItem.carbs}"

        val fat = itemView?.findViewById<TextView>(R.id.food_fat)
        fat?.text = "Fat: ${currentItem.fat}"

        val btn = itemView?.findViewById<android.widget.Button>(R.id.action_btn)

        if(isAddFood) {
            btn?.setBackgroundResource(R.drawable.add_btn)
            btn?.setOnClickListener{
                lifecycleScope.launch {
                    try {
                        if (isFromPostFragment) {
                            addFoodToPost(currentItem)
                        } else {
                            // addFood(currentItem, activity)
                        }
                    } catch (e: Exception) {
                        Log.e("ERROR", "Failed to fetch data", e)
                    }
                }
            }
        } else {
            btn?.setBackgroundResource(R.drawable.delete_btn)
            btn?.setOnClickListener {
                //delete the food from db
                lifecycleScope.launch {
                    try {
                        if (isFromPostFragment) {
                            // deleteFoodFromPost(currentItem)
                        } else {
                            deleteFood("allison", "breakfast", currentItem.name)
                        }
                    } catch (e: Exception) {
                        Log.e("ERROR", "Failed to fetch data", e)
                    }
                }
            }
        }
        return itemView!!
    }

    private suspend fun deleteFood(username: String, meal: String, foodItem: String): Boolean {
        val url = "${BASE_URL}logger/delete"
        val completableDeferred = CompletableDeferred<Boolean>()

        Log.i("DATA", "given name: ${foodItem}")

        val params = JSONObject().apply {
            // Add your body parameters here
            put("username", username)
            put("foodItem", foodItem)
            put("meal", meal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                Log.i("DATA", response.toString())
                completableDeferred.complete(true)
            },
            { error ->
                Log.e("ERROR", "Error: $error")
                completableDeferred.completeExceptionally(error)
                completableDeferred.complete(false)
            }
        )
        VolleyService.getInstance(activity).add(request)

        return completableDeferred.await()
    }

//    private suspend fun addFood(name: String, cal: String, meal: String, serving: String, nutrition: Array<Int>, activity: Activity): String {
//        val url = "${BASE_URL}logger"
//        val completableDeferred = CompletableDeferred<String>()
//
//        val params = JSONObject().apply {
//            // Add your body parameters here
//            put("username", "alicesmith")
//            put("foodId", name)
//            put("meal", meal)
//            put("calories", cal)
//            put("servingSize", serving)
//            put("nutrition", nutrition)
//        }
//
//        val request = JsonObjectRequest(
//            Request.Method.POST, url, params,
//            { response ->
//                val logged = JsonParser().parseLogger(response.toString())
//                Log.i("DATA", logged.toString())
//                completableDeferred.complete("successfully delete food")
//            },
//            { error ->
//                Log.e("ERROR", "Error: $error")
//                completableDeferred.completeExceptionally(error)
//            }
//        )
//        VolleyService.getInstance(activity).add(request)
//
//        return completableDeferred.await()
//    }

    private fun addFoodToPost(foodItem: FoodItem) {
        // restart the post fragment with foodItem
        val postFragment = PostFragment()
        val bundle = Bundle().apply {
            putString("foodItem", foodItem.toString())
        }
        postFragment.arguments = bundle
        (activity as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.container, postFragment)
            .commit()
    }
}