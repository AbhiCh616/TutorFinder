package com.example.tutorfinder

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentActivity: AppCompatActivity() {

    private lateinit var bottomView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_activity)

        bottomView = findViewById(R.id.bottom_navigation)
        bottomView.setOnNavigationItemSelectedListener(privateListener)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StudentSearchFragment())
            .commit()
    }

    private val privateListener =
        BottomNavigationView.OnNavigationItemSelectedListener() { item: MenuItem ->
            val fragment =
                when(item.itemId) {
                    R.id.chat -> StudentChatFragment()
                    R.id.profile -> StudentProfileFragment()
                    else -> StudentSearchFragment()
                }

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()

            true
        }

}