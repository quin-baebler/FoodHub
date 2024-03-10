package edu.uw.ischool.xyou.foodhub.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.Comment
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

class PostDetailActivity : AppCompatActivity() {

    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api/"

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
        val recipeIds = intent.getStringArrayListExtra("recipeIds")
        val likes = intent.getStringArrayListExtra("likes")

        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        renderPostDetails(title!!, calories, descr!!, date!!, likes!!.size)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val comments = fetchComments(postId!!)
                Log.d(TAG, "Comments: $comments")

                renderCommentList(comments)

                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(this@PostDetailActivity, "Error loading comments, please try again", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun renderPostDetails(title: String, calories: Int, descr: String, date: String, likes: Int) {
        val titleTextView = findViewById<TextView>(R.id.post_detail_title)
        val caloriesTextView = findViewById<TextView>(R.id.post_detail_calories)
        val descrTextView = findViewById<TextView>(R.id.post_detail_descr)
        val dateTextView = findViewById<TextView>(R.id.post_detail_date)
        val likesTextView = findViewById<TextView>(R.id.like_count)

        titleTextView.text = title
        caloriesTextView.text = calories.toString()
        descrTextView.text = descr
        dateTextView.text = date.substring(0, 10)
        likesTextView.text = likes.toString()
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

    private fun renderCommentList(comments: List<Comment>) {
        val listView = findViewById<ListView>(R.id.comment_list)

        val data = ArrayList<HashMap<String, String>>()
        for (comment in comments) {
            val map = HashMap<String, String>()
            map["username"] = comment.username
            map["date"] = comment.date.substring(0, 10)
            map["comment"] = comment.comment
            data.add(map)
        }

        val from = arrayOf("username", "date", "comment")
        val to = intArrayOf(R.id.comment_item_username, R.id.comment_item_date, R.id.comment_item_comment)

        val adapter = SimpleAdapter(this, data, R.layout.comment_item, from, to)
        listView.adapter = adapter
    }
}