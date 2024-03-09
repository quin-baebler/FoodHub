package edu.uw.ischool.xyou.foodhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.yourappname.LoginFragment
import edu.uw.ischool.xyou.foodhub.databinding.ActivityMainBinding
import edu.uw.ischool.xyou.foodhub.home.HomeFragment
import edu.uw.ischool.xyou.foodhub.logger.LoggerFragment
import edu.uw.ischool.xyou.foodhub.post.PostFragment

class MainActivity : AppCompatActivity(), FragmentNavigationListener{

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize the app with the home fragment
        replaceFragment(LoginFragment())
        hideNavigationBar()
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.post -> replaceFragment(PostFragment())
                R.id.logger -> replaceFragment(LoggerFragment())
                else -> {}
            }
            true
        }
        val loginFragment = LoginFragment()
        loginFragment.setFragmentNavigationListener(this)
       // showNavigationBar()
    }
    private fun showNavigationBar() {
        binding.bottomNavigation.visibility = View.VISIBLE
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