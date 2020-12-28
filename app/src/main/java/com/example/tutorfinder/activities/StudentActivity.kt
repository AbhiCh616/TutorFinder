package com.example.tutorfinder.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.example.tutorfinder.fragments.StudentChatFragment
import com.example.tutorfinder.fragments.StudentProfileFragment
import com.example.tutorfinder.fragments.StudentSearchFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StudentActivity: AppCompatActivity() {

    companion object {
        private val TAG = StudentActivity::class.qualifiedName
    }

    // Declare views
    private lateinit var bottomView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_screen)

        // Set views
        bottomView = findViewById(R.id.bottom_navigation)
        bottomView.setOnNavigationItemSelectedListener(privateListener)

        // To show StudentSearchFragment when the activity starts
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StudentSearchFragment())
            .commit()
    }

    // To change fragments according to bottom navigation
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