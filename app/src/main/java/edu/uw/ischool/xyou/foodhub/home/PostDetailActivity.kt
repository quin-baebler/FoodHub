package edu.uw.ischool.xyou.foodhub.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.Comment
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.xyou.foodhub.data.Food

class PostDetailActivity : AppCompatActivity() {

    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api"

    private val TAG = "PostDetailActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        // retrieve the post from the intent
        val postId = intent.getStringExtra("postId")
        val title = intent.getStringExtra("title")
        val calories = intent.getIntExtra("calories", 0)
        val descr = intent.getStringExtra("descr")
        val date = intent.getStringExtra("date")
        val foodIds = intent.getStringArrayListExtra("foodIds")
        val likes = intent.getStringArrayListExtra("likes")

        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        renderPostDetails(title!!, calories, descr!!, date!!, likes!!, username!!)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

//        loadFoodItems(foodIds!!, progressBar)
        loadComments(postId!!, progressBar)

        val commentInput = findViewById<EditText>(R.id.comment_input)
        val sendButton = findViewById<Button>(R.id.submit_comment_button)
        val likeButton = findViewById<ImageView>(R.id.like_button)
        val likeNumber = findViewById<TextView>(R.id.like_count)
        // when user have input, enable the send button
        commentInput.addTextChangedListener {
            sendButton.visibility = if (it.toString().isNotEmpty()) View.VISIBLE else View.GONE
        }

        sendButton.setOnClickListener {
            addComment(postId, username, commentInput.text.toString(), progressBar)
            commentInput.text.clear()
            commentInput.clearFocus()
        }

        likeButton.setOnClickListener {
            Log.i(TAG, "onCreate: like button clicked")
            toggleLike(postId, username, likeButton, likeNumber, likes)
        }
    }

    private fun toggleLike(postId: String, username: String, likeButton: ImageView, likeNumber: TextView, likes: ArrayList<String>) {
        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("postId", postId)
            put("username", username)
        }

        var url = "$BASE_URL/posts"
        // ui updates
        if (likes.contains(username)) {
            url += "/unlike"
            likeButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.outline_thumb_up_24))
            likes.remove(username)
        } else {
            url += "/like"
            likeButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_thumb_up_24))
            likes.add(username)
        }
        likeNumber.text = likes.size.toString()

        val request = JsonObjectRequest(Request.Method.POST, url, jsonRequest,
            {response ->
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
            },
            {error ->
                Log.e(TAG, "Error: $error")
                Toast.makeText(this, "Operation failed, please try again", Toast.LENGTH_SHORT).show()
            })

        VolleyService.getInstance(this).add(request)
    }

//    private fun loadFoodItems(foodIds: ArrayList<String>, progressBar: ProgressBar) {
//        lifecycleScope.launch {
//            try {
//                val foodItems = fetchFoodItems(foodIds)
//
//                val recyclerView = findViewById<RecyclerView>(R.id.food_list)
//                recyclerView.adapter = FoodAdapter(foodItems)
//                recyclerView.layoutManager = LinearLayoutManager(this@PostDetailActivity)
//
//                progressBar.visibility = View.GONE
//            } catch (e: Exception) {
//                Toast.makeText(this@PostDetailActivity, "Error loading food items, please try again", Toast.LENGTH_SHORT).show()
//                progressBar.visibility = View.GONE
//            }
//        }
//    }

    private fun loadComments(postId: String, progressBar: ProgressBar) {
        lifecycleScope.launch {
            try {
                val comments = fetchComments(postId)

                val recyclerView = findViewById<RecyclerView>(R.id.comment_list)
                recyclerView.adapter = CommentAdapter(comments)
                recyclerView.layoutManager = LinearLayoutManager(this@PostDetailActivity)

                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(this@PostDetailActivity, "Error loading comments, please try again", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun renderPostDetails(title: String, calories: Int, descr: String, date: String, likes: ArrayList<String>, username: String) {
        val titleTextView = findViewById<TextView>(R.id.post_detail_title)
        val caloriesTextView = findViewById<TextView>(R.id.post_detail_calories)
        val descrTextView = findViewById<TextView>(R.id.post_detail_descr)
        val dateTextView = findViewById<TextView>(R.id.post_detail_date)
        val likeButtonImageView = findViewById<ImageView>(R.id.like_button)
        val likesTextView = findViewById<TextView>(R.id.like_count)

        titleTextView.text = title
        caloriesTextView.text = "Total calories: ${calories}"
        descrTextView.text = descr
        dateTextView.text = date.substring(0, 10)
        likeButtonImageView.setImageDrawable(
            if (likes.contains(username)) {
            AppCompatResources.getDrawable(this, R.drawable.baseline_thumb_up_24)
        } else {
            AppCompatResources.getDrawable(this, R.drawable.outline_thumb_up_24)
        })
        likesTextView.text = likes.size.toString().toInt().toString()
    }

    private suspend fun fetchFoodItems(foodIds: ArrayList<String>): List<Food> {
        val url = "$BASE_URL/foods"
        val completableDeferred = CompletableDeferred<List<Food>>()

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val foodItems = JsonParser().parseFood(response.toString())
                completableDeferred.complete(foodItems)
            },
            { error ->
                Log.e(TAG, "Error: $error")
                completableDeferred.completeExceptionally(error)
            }
        )
        VolleyService.getInstance(this).add(request)

        return completableDeferred.await()
    }

    private suspend fun fetchComments(postId: String): List<Comment> {
        val url = "$BASE_URL/posts/comments?postId=$postId"
        val completableDeferred = CompletableDeferred<List<Comment>>()

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val comments = JsonParser().parseComments(response.toString())
                completableDeferred.complete(comments)
            },
            { error ->
                Log.e(TAG, "Error: $error")
                completableDeferred.completeExceptionally(error)
            }
        )
        VolleyService.getInstance(this).add(request)

        return completableDeferred.await()
    }

    private fun addComment(postId: String, username: String, comment: String, progressBar: ProgressBar) {
        val jsonRequest = JSONObject()
        jsonRequest.apply {
            put("postId", postId)
            put("username", username)
            put("comment", comment)
        }

        val url = "$BASE_URL/posts/comment"

        val request = JsonObjectRequest(Request.Method.POST, url, jsonRequest,
            {response ->
                Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.VISIBLE
                loadComments(postId, progressBar)
            },
            { error ->
                Log.e(TAG, "Error posting:", error)
                Toast.makeText(this, "Operation failed, please try again", Toast.LENGTH_SHORT).show()
            }
        )
        VolleyService.getInstance(this).add(request)
    }
}