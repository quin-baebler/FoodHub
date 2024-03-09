package edu.uw.ischool.xyou.foodhub.login

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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import edu.uw.ischool.xyou.foodhub.R
import edu.uw.ischool.xyou.foodhub.utils.VolleyService
import org.json.JSONObject

class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"
    private val BASE_URL = "https://foodhub-backend.azurewebsites.net/api"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameInput = view.findViewById<EditText>(R.id.usernameInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        
        loginButton.setOnClickListener {
            // Validate email and password
            if (usernameInput.text.toString().isEmpty() || passwordInput.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        
            // Create a JSON object for the login request
            val jsonRequest = JSONObject()
            jsonRequest.put("username", usernameInput.text.toString())
            jsonRequest.put("password", passwordInput.text.toString())

            // Create a Volley request to your backend API
            val url = "$BASE_URL/users/login"
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
                    editor.apply {
                        putString("username", username)
                        putString("dob", dob)
                        putInt("height", height)
                        putInt("weight", weight)
                        putString("healthGoal", healthGoal)
                        putString("activityLevel", activityLevel)
                        apply()
                    }

                    Toast.makeText(requireContext(), "$message, welcome $username!", Toast.LENGTH_SHORT).show()
                },
                { error ->
                    // Handle login error
                    Log.e(TAG, "Error during login:", error)
                    Toast.makeText(requireContext(), "Login failed, please check your credentials", Toast.LENGTH_SHORT).show()
                }
            )
            VolleyService.getInstance(requireActivity()).add(request)
        }
    }
}