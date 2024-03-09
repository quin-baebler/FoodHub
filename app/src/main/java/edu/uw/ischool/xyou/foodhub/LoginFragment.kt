package com.example.yourappname

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
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.uw.ischool.xyou.foodhub.FragmentNavigationListener
import edu.uw.ischool.xyou.foodhub.HomeFragment
import edu.uw.ischool.xyou.foodhub.R
import org.json.JSONObject

class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private lateinit var queue: RequestQueue
    private lateinit var fragmentNavigationListener: FragmentNavigationListener



    override fun onAttach(context: Context) {
        super.onAttach(context)
        queue = Volley.newRequestQueue(context) // Initialize queue in onAttach
        if (context is FragmentNavigationListener) {
            fragmentNavigationListener = context
        } else {
            throw IllegalArgumentException("Context must implement FragmentNavigationListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    fun setFragmentNavigationListener(listener: FragmentNavigationListener) {
        fragmentNavigationListener = listener
    }
    fun onLoginSuccess() {
        fragmentNavigationListener.replaceFragment(HomeFragment())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
        
            // Validate email and password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        
            // Create a JSON object for the login request
            val jsonRequest = JSONObject()
            jsonRequest.put("username", email) // Assuming your backend uses "username" for login
            jsonRequest.put("password", password)

            // Create a Volley request to your backend API
            val url = "https://foodhub-backend.azurewebsites.net/api/users/login"
            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonRequest,
                { response ->
                    // Handle successful login response
                    val message = response.getString("message")
                    val user = response.getJSONObject("user")
                    // Extract user data
                    val username = user.getString("username")
                    val dob = user.getString("dob") // Assuming "dob" key exists
                    val height = user.getInt("height") // Assuming "height" key exists (int)
                    val weight = user.getInt("weight") // Assuming "weight" key exists (int)
                    val healthGoal = user.getString("healthGoal")
                    val activityLevel = user.getString("activityLevel")

                    // Store user data in SharedPreferences
                    val sharedPreferences = requireActivity().getSharedPreferences("userData", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("username", username)
                    editor.putString("dob", dob)
                    editor.putInt("height", height)
                    editor.putInt("weight", weight)
                    editor.putString("healthGoal", healthGoal)
                    editor.putString("activityLevel", activityLevel)
                    editor.apply()

                    Toast.makeText(requireContext(), "$message, welcome $username!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                },
                { error ->
                    // Handle login error
                    Log.e(TAG, "Error during login:", error)
                    Toast.makeText(requireContext(), "Login failed, please check your credentials", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(request)
        }
    }
}