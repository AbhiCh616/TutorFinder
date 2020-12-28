package com.example.tutorfinder.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.example.tutorfinder.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class TutorActivity: AppCompatActivity() {

    companion object {
        private val TAG = TutorActivity::class.qualifiedName
    }

    // Declare views
    private lateinit var bottomView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_screen)

        // Set views
        bottomView = findViewById(R.id.bottom_navigation)
        bottomView.setOnNavigationItemSelectedListener(privateListener)

        // To show StudentSearchFragment when the activity starts
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TutorChatFragment())
                .commit()
    }

    // To change fragments according to bottom navigation
    private val privateListener =
            BottomNavigationView.OnNavigationItemSelectedListener() { item: MenuItem ->
                val fragment =
                        when(item.itemId) {
                            R.id.profile -> TutorProfileFragment()
                            else -> TutorChatFragment()
                        }

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit()

                true
            }
}