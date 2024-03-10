package edu.uw.ischool.xyou.foodhub.logger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import edu.uw.ischool.xyou.foodhub.R

class CustomListAdapter(context: Context, val itemList: List<ArrayList<String>>, val isAddFood: Boolean) :
    ArrayAdapter<List<String>>(context, R.layout.list_item_layout, itemList) {
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
            }

            return itemView!!
        }
}