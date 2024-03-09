package edu.uw.ischool.xyou.foodhub

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import edu.uw.ischool.xyou.foodhub.databinding.ActivityMainBinding
import edu.uw.ischool.xyou.foodhub.home.HomeFragment
import edu.uw.ischool.xyou.foodhub.logger.LoggerFragment
import edu.uw.ischool.xyou.foodhub.login.LoginFragment
import edu.uw.ischool.xyou.foodhub.post.PostFragment
import edu.uw.ischool.xyou.foodhub.utils.FragmentNavigationListener

class MainActivity : AppCompatActivity(), FragmentNavigationListener {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get preferences
        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("username")) {
            replaceFragment(HomeFragment())
        } else {
            replaceFragment(LoginFragment())
            hideNavigationBar()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.post -> replaceFragment(PostFragment())
                R.id.logger -> replaceFragment(LoggerFragment())
                else -> {}
            }
            true
        }
    }

    private fun hideNavigationBar() {
        binding.bottomNavigation.visibility = View.GONE
    }

    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}