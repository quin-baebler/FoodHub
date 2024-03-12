package edu.uw.ischool.xyou.foodhub.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import edu.uw.ischool.xyou.foodhub.MainActivity
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.FoodItem
import edu.uw.ischool.xyou.foodhub.home.FoodAdapter
import edu.uw.ischool.xyou.foodhub.home.HomeFragment
import edu.uw.ischool.xyou.foodhub.logger.AddFood
import edu.uw.ischool.xyou.foodhub.logger.CustomListAdapter
import edu.uw.ischool.xyou.foodhub.utils.DataRepository
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [PostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostFragment : Fragment() {

    private val TAG = "PostFragment"
    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        // get data from another fragment if there is any
        var foodItem = arguments?.getString("foodItem")

        foodItem?.let {
            DataRepository.addData(Gson().fromJson(it, FoodItem::class.java))
            renderFoodItems(view, DataRepository.getFoodItems())
        }


        // collect user input
        val titleInput = view.findViewById<EditText>(R.id.title_input)
        val descrInput = view.findViewById<EditText>(R.id.descr_input)
        val caloriesInput = view.findViewById<EditText>(R.id.calories_input)

        val postButton = view.findViewById<Button>(R.id.post_button)

        postButton.setOnClickListener {
            val title = titleInput.text.toString()
            val descr = descrInput.text.toString()
            val calories = caloriesInput.text.toString().toInt()

            lifecycleScope.launch {
                val response = createPost(username!!, title, descr, calories)
                if (response == "Post created") {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment())
                        .commit()
                }
            }
        }

        val addFoodButton = view.findViewById<ImageView>(R.id.add_btn)
        addFoodButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddFood().apply { arguments = Bundle().apply { putBoolean("isFromPostFragment", true) } })
                .commit()
        }
    }

    private suspend fun createPost(username: String, title: String, descr: String, calories: Int) : String {
        val url = "$BASE_URL/posts"
        val completableDeferred = CompletableDeferred<String>()

        // create a list of recipe ids
        val recipeIds = mutableListOf<String>()
        DataRepository.getFoodItems().forEach {
            recipeIds.add(it.foodId)
        }

        // create a json object
        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("username", username)
            put("title", title)
            put("descr", descr)
            put("calories", calories)
            put("recipeIds", JSONArray(recipeIds))
        }

        Log.i(TAG, "createPost: $jsonRequest")

        val request = JsonObjectRequest(Request.Method.POST, url, jsonRequest,
            {response ->
                val message = response.getString("message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                completableDeferred.complete(message)
            },
            { error ->
                Log.e(TAG, "Error posting:", error)
                Toast.makeText(context, "Operation failed, please try again", Toast.LENGTH_SHORT).show()

                completableDeferred.completeExceptionally(error)
            })

        VolleyService.getInstance(requireActivity()).add(request)

        // clear the data
        DataRepository.clearData()

        return completableDeferred.await()
    }

    private fun renderFoodItems(view: View, foodItems: List<FoodItem>) {
        Log.i(TAG, "renderFoodItems: $foodItems")
        val recyclerView = view.findViewById<RecyclerView>(R.id.attach_food_rv)
        recyclerView.adapter = FoodAdapter(foodItems)
        // horizontal layout
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
}