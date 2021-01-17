package com.example.tutorfinder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.example.tutorfinder.fragments.TutorBasicInfoRegFragment

class TutorRegistration: AppCompatActivity() {

    companion object {
        private val TAG = TutorRegistration::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration_activity)

        // Select first fragment to appear
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TutorBasicInfoRegFragment())
                .commit()
    }

}