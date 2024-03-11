package edu.uw.ischool.xyou.foodhub.logger

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
import edu.uw.ischool.xyou.foodhub.data.Logger
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

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

    private var logInfo : Logger = Logger("", 0, listOf(), listOf())
    private val viewLog = ViewLog()

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
        setUpCards(view)
    }

    private fun setUpCards(view: View) {
        lifecycleScope.launch {
            try {
                logInfo = fetchLoggerData()
                Log.i("PLS", logInfo.toString())

                val calPerMeal = logInfo.mealInfo
                val calories = arrayOf(R.id.breakfast_cal, R.id.lunch_cal, R.id.snack_cal, R.id.dinner_cal)

                for (i in calories.indices) {
                    val mealCal = view.findViewById<TextView>(calories[i])
                    mealCal.text = "Total calories: ${calPerMeal.get(i)?.mealCal} cal"
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

                val meal = arrayOf("Breakfast", "Lunch", "Snack", "Dinner")

                for (j in cards.indices) {
                    val bundle = Bundle()
                    bundle.putString("meal", meal[j])
                    bundle.putString("mealCal", logInfo.mealInfo[j].mealCal)
                    bundle.putIntArray("nutrition", logInfo.mealInfo[j].nutrition.toIntArray())

                    for (k in logInfo.mealInfo[j].food.indices) {
                        val anItem = arrayListOf<String>()

                        val foodName = logInfo.mealInfo[j].food[k].name
                        val foodCal = logInfo.mealInfo[j].food[k].calorie
                        val foodServing = logInfo.mealInfo[j].food[k].serving
                        val foodNutrition = logInfo.mealInfo[j].food[k].nutrition

                        anItem.add(foodName)
                        anItem.add(foodCal)
                        anItem.add(foodServing)
                        anItem.add(foodNutrition.toString())

                        bundle.putStringArrayList("food_${k}", anItem)
                    }

                    val card = view.findViewById<LinearLayout>(cards[j])
                    card.setOnClickListener {
                        viewLog.arguments = bundle
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, viewLog)?.commit()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Failed to fetch data", e)
            }
        }

    }

    private suspend fun fetchLoggerData(): Logger {
        val url = "${BASE_URL}logger?username=alicesmith"
        val completableDeferred = CompletableDeferred<Logger>()

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val logged = JsonParser().parseLogger(response.toString())
                Log.i("DATA", logged.toString())
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