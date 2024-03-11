package edu.uw.ischool.xyou.foodhub.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.data.Post
import edu.uw.ischool.xyou.foodhub.utils.JsonParser
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), PostAdapterInterface {
    private val TAG = "HomeFragment"
    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")

        val sharedPreferences = requireActivity().getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        val welcomeTextView = view.findViewById<TextView>(R.id.welcome_text)
        welcomeTextView.text = getString(R.string.welcome, username)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val posts = fetchPostData()
                Log.d(TAG, "Post data: $posts")

                val recyclerView = view.findViewById<RecyclerView>(R.id.rv_posts)
                recyclerView.adapter = PostAdapter(posts, this@HomeFragment)
                recyclerView.layoutManager = LinearLayoutManager(context)

                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading, please try again", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onItemClicked(post: Post) {
        val intent = Intent(activity, PostDetailActivity::class.java).apply {
            putExtra("postId", post.id)
            putExtra("title", post.title)
            putExtra("calories", post.calories)
            putExtra("date", post.date)
            putExtra("descr", post.descr)
            putExtra("recipeIds", post.recipeIds as ArrayList<String>)
            putExtra("likes", post.likes as ArrayList<String>)
        }
        startActivity(intent)
    }

    private suspend fun fetchPostData(): List<Post> {
        val url = "$BASE_URL/posts"
        val completableDeferred = CompletableDeferred<List<Post>>()

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val posts = JsonParser().parsePosts(response.toString())
                completableDeferred.complete(posts)
            },
            { error ->
                Log.e(TAG, "Error: $error")
                completableDeferred.completeExceptionally(error)
            }
        )
        VolleyService.getInstance(requireActivity()).add(request)

        return completableDeferred.await()
    }
}