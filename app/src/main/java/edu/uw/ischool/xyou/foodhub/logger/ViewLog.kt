package edu.uw.ischool.xyou.foodhub.logger

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.Food
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.json.JSONObject

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
        val mealCal = arguments?.getString("mealCal")
        val foodList = arrayListOf<ArrayList<String>>()

        arguments?.keySet()?.forEach { key ->
            if(key.startsWith("food_")) {
                val oneItem = arguments?.getStringArrayList(key)
                if (oneItem != null) {
                    foodList.add(oneItem)
                }
            }
        }

        Log.i("DONE?", foodList.toString())

        val title = view.findViewById<TextView>(R.id.view_log_header)
        val cal = view.findViewById<TextView>(R.id.meal_cal)
        val itemsView = view.findViewById<ListView>(R.id.logged_items)

        title.text = mealName
        cal.text = "Calories: ${mealCal}"

        val adapter = CustomListAdapter(requireContext(), foodList, false)
        itemsView.adapter = adapter

    }


}