package com.example.tutorfinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class SelectRoleActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = SelectRoleActivity::class.qualifiedName
    }

    // Firebase authentication
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    // Views
    private lateinit var selectStudentButton: Button
    private lateinit var selectTeacherButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_role)

        // Authentication
        auth = Firebase.auth

        // Views instantiated
        selectStudentButton = findViewById(R.id.student)
        selectTeacherButton = findViewById(R.id.teacher)

        // click listener set
        selectStudentButton.setOnClickListener(this)
        selectTeacherButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        // Get logged in user
        user = auth.currentUser
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.student -> triggerStudentRegistration()
            R.id.teacher -> triggerTutorRegistration()
        }
    }

    // If new user is tutor
    private fun triggerTutorRegistration() {
        val tutorIntent = Intent(this, TutorRegistrationActivity::class.java)
        startActivity(tutorIntent)
    }

    // If new user is student
    private fun triggerStudentRegistration() {
        // Update user profile photo to "Student"
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse("Student")
        }
        user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Student image updated.")
            }
        }

        // Start student registration activity
        val studentIntent = Intent(this, StudentRegistrationActivity::class.java)
        startActivity(studentIntent)
    }
}