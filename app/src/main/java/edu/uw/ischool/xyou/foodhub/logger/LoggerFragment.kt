package edu.uw.ischool.xyou.foodhub.logger

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.FoodItem
import edu.uw.ischool.xyou.foodhub.data.Logger
import edu.uw.ischool.xyou.foodhub.data.Meal
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log
import com.google.gson.Gson

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoggerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoggerFragment : Fragment() {
    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api/"

    private var logInfo : Logger = Logger("", "", arrayListOf(), hashMapOf(), 0)
    private val viewLog = ViewLog()
    private var totalProtein = 0.0
    private var totalCarbs = 0.0
    private var totalFat = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logger, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireActivity().getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        setUpCards(view, username!!)
    }

    private fun setUpCards(view: View, username: String) {
        lifecycleScope.launch {
            try {
                logInfo = fetchLoggerData(username)
                Log.i("PLS", logInfo.toString())
                val meal = arrayOf("breakfast", "lunch", "snack", "dinner")

                val meals = logInfo.meals

                Log.i("TIRED", meals.toString())

                val calories = arrayOf(R.id.breakfast_cal, R.id.lunch_cal, R.id.snack_cal, R.id.dinner_cal)

                for (i in calories.indices) {
                    val mealCal = view.findViewById<TextView>(calories[i])

                    // Find the index of the meal
                    val mealIndex = meals.indexOfFirst { it.name == meal[i] }

                    // Update the card
                    if (mealIndex != -1) {
                        val totalCal = meals[mealIndex].totalCal
                        mealCal.text = "Total calories: $totalCal cal"
                    } else {
                        // if no food logged yet, set total cal to 0
                        mealCal.text = "Total calories: 0 cal"
                    }
                }

                // there has to be a better way to do this
                val btns = arrayOf(R.id.breakfast_btn, R.id.lunch_btn, R.id.snack_btn, R.id.dinner_btn)
                val cards = arrayOf(R.id.breakfast_card, R.id.lunch_card, R.id.snack_card, R.id.dinner_card)

                for (btn in btns.indices) {
                    val addBtn = view.findViewById<android.widget.Button>(btns[btn])
                    addBtn.setOnClickListener{
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, AddFood())?.commit()
                    }
                }

                for (j in cards.indices) {
                    val bundle = Bundle()
                    bundle.putString("meal", meal[j])

                    val mealIndex = logInfo.meals.indexOfFirst { it.name == meal[j] }

                    var data = 0

                    var foodListJson = "[]"

                    if (mealIndex != -1) {
                        data = logInfo.meals[mealIndex].totalCal
                        val mealFoods = logInfo.meals[mealIndex].foods ?: emptyList()

                        if (mealFoods.isNotEmpty()) {
                            val gson = Gson()
                            foodListJson = gson.toJson(mealFoods)
                        }
                    }

                    bundle.putString("foodList", foodListJson)
                    bundle.putInt("mealCal", data)

                    val card = view.findViewById<LinearLayout>(cards[j])
                    card.setOnClickListener {
                        viewLog.arguments = bundle
                        Log.i("FUCK", "correct bundle?  ${viewLog.arguments.toString()}")
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, viewLog)?.commit()
                    }
                }

                for (oneMeal in meals.indices) {
                    getTotalNutritionInfo(view, meals[oneMeal].foods)
                }

            } catch (e: Exception) {
                Log.e("ERROR", "Failed to fetch data", e)
            }
        }

    }

    private fun getTotalNutritionInfo(view: View, foodList: ArrayList<FoodItem>) {
        val nutritionInfo = arrayOf(R.id.protein, R.id.carbs, R.id.fat)

        for (i in foodList.indices) {
            totalProtein += foodList[i].protein
            totalCarbs += foodList[i].carbs
            totalFat += foodList[i].fat
        }

        val proteinTab = view.findViewById<TextView>(nutritionInfo[0])
        proteinTab.text = totalProtein.toString()
        val carbsTab = view.findViewById<TextView>(nutritionInfo[1])
        carbsTab.text = totalCarbs.toString()
        val fatTab = view.findViewById<TextView>(nutritionInfo[2])
        fatTab.text = totalFat.toString()
    }
    private suspend fun fetchLoggerData(username: String): Logger {
        val url = "${BASE_URL}logger?username=${username}"
        val completableDeferred = CompletableDeferred<Logger>()

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.i("WHY", response.toString())
                val logged = JsonParser().parseLogger(response.toString())
                completableDeferred.complete(logged)
            },
            { error ->
                Log.e("ERROR", "Error: $error")
                completableDeferred.completeExceptionally(error)
            }
        )
        VolleyService.getInstance(requireActivity()).add(request)

        return completableDeferred.await()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoggerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoggerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}