package com.example.tutorfinder.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.example.tutorfinder.fragments.TutorRegBasicInfo
import com.example.tutorfinder.fragments.TutorRegLocation
import com.example.tutorfinder.fragments.TutorRegOptionalFragment
import com.example.tutorfinder.fragments.TutorRegSubject
import com.google.android.material.imageview.ShapeableImageView

class TutorRegistration: AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = TutorRegistration::class.qualifiedName
    }

    // Manage Fragments
    private var presentFragmentNumber: Int = 0
    private val fragmentList = listOf<Fragment>(TutorRegBasicInfo(), TutorRegLocation(), TutorRegSubject(), TutorRegOptionalFragment())

    // Views
    private lateinit var nextButton: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration_activity)

        // Initialize views
        nextButton = findViewById(R.id.next_button)

        // Set on click listener
        nextButton.setOnClickListener(this)

        // Select first fragment to appear
        startFragment(0)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.next_button -> changeFragment()
        }
    }

    private fun changeFragment() {
        // If we are not at the last fragment of the list
        if(++presentFragmentNumber < fragmentList.size) {
            startFragment(presentFragmentNumber)
        }
        else {
            --presentFragmentNumber
        }
    }

    // Show the fragment at the given number of the list
    private fun startFragment(i: Int) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentList[i])
                .commit()
    }

    override fun onBackPressed() {
        // If we are on the first fragment, go to previous activity
        if(presentFragmentNumber == 0) {
            super.onBackPressed()
        }
        // Go to previous fragment
        else {
            startFragment(--presentFragmentNumber)
        }
    }

}