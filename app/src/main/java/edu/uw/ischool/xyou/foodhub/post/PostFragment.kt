package edu.uw.ischool.xyou.foodhub.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.home.HomeFragment
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
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
                val response = postThread(username!!, title, descr, calories)
                if (response == "Post created") {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment())
                        .commit()
                }
            }
        }
    }

    private suspend fun postThread(username: String, title: String, descr: String, calories: Int) : String {
        val url = "$BASE_URL/posts"
        val completableDeferred = CompletableDeferred<String>()

        // create a json object
        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("username", username)
            put("title", title)
            put("descr", descr)
            put("calories", calories)
        }

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

        return completableDeferred.await()
    }
}