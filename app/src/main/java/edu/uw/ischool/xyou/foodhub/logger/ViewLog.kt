package edu.uw.ischool.xyou.foodhub.logger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.FoodItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewLog: Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mealName = arguments?.getString("meal")
        val mealCal = arguments?.getInt("mealCal")
        val foodListStr = arguments?.getString("foodList")

        if (foodListStr != null) {
            Log.i("BUT", foodListStr)
        }

        // Convert JSON string back to FoodItem list
        val foodListType = object : TypeToken<List<FoodItem>>() {}.type
        val foodList = Gson().fromJson<List<FoodItem>>(foodListStr, foodListType)


        val title = view.findViewById<TextView>(R.id.view_log_header)
        val cal = view.findViewById<TextView>(R.id.meal_cal)
        val itemsView = view.findViewById<ListView>(R.id.logged_items)

        //there has to be a better way to do this
        val hateKotlin = hashMapOf<String, String>("breakfast" to "Breakfast", "lunch" to "Lunch",
                                                    "snack" to "Snack", "dinner" to "Dinner")
        title.text = hateKotlin[mealName]
        cal.text = "Calories: ${mealCal}"

        val adapter = CustomListAdapter(requireContext(), requireActivity(), lifecycleScope, foodList, false, mealName!!)
        itemsView.adapter = adapter

    }

}