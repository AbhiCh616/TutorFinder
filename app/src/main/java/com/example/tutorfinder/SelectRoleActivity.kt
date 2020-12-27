package com.example.tutorfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class SelectRoleActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = SelectRoleActivity::class.qualifiedName
    }

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private lateinit var selectStudentButton: Button
    private lateinit var selectTeacherButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_role)

        selectStudentButton = findViewById(R.id.student)
        selectTeacherButton = findViewById(R.id.teacher)

        selectStudentButton.setOnClickListener(this)
        selectTeacherButton.setOnClickListener(this)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.student -> triggerStudentFlow()
            R.id.teacher -> triggerTeacherFlow()
        }
    }

    private fun triggerTeacherFlow() {
        TODO("Not yet implemented")
    }

    private fun triggerStudentFlow() {
        // Update user profile photo to "Student"
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse("Student")
        }
        user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Student image updated.")
            }
        }

        val studentIntent = Intent(this, StudentRegistrationActivity::class.java)
        startActivity(studentIntent)
    }
}