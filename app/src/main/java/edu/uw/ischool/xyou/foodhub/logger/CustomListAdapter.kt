package edu.uw.ischool.xyou.foodhub.logger

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.Logger
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject

class CustomListAdapter(context: Context, val itemList: List<ArrayList<String>>, val isAddFood: Boolean) :
    ArrayAdapter<List<String>>(context, R.layout.list_item_layout, itemList) {
    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api/"
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false)
            }

            val currentItem = itemList[position]
            val title = itemView?.findViewById<TextView>(R.id.food_name)
            title?.text = currentItem[0]

            val cal = itemView?.findViewById<TextView>(R.id.food_cal)
            cal?.text = "Calories: ${currentItem[1]}"

            val btn = itemView?.findViewById<android.widget.Button>(R.id.action_btn)
            if(isAddFood) {
                btn?.setBackgroundResource(R.drawable.add_btn)
            }else{
                btn?.setBackgroundResource(R.drawable.delete_btn)
                btn?.setOnClickListener {
                    //delete the food from db
                }
            }

            return itemView!!
        }

//    private suspend fun fetchLoggerData(): Logger {
//        val url = "${BASE_URL}logger/delete"
//        val completableDeferred = CompletableDeferred<Logger>()
//
//        val params = JSONObject().apply {
//            // Add your body parameters here
//            put("username", "alicesmith")
//            put("foodName", "egg")
//        }
//
//        val request = JsonObjectRequest(
//            Request.Method.POST, url, params,
//            { response ->
//                val logged = JsonParser().parseLogger(response.toString())
//                Log.i("DATA", logged.toString())
//                completableDeferred.complete(logged)
//            },
//            { error ->
//                Log.e("ERROR", "Error: $error")
//                completableDeferred.completeExceptionally(error)
//            }
//        )
//        VolleyService.getInstance(requireActivity()).add(request)
//
//        return completableDeferred.await()
//    }
}