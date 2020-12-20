package com.example.tutorfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class SelectRoleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth:FirebaseAuth
    private val TAG = "SelectRoleActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_role)

        findViewById<Button>(R.id.student).setOnClickListener(this)
        findViewById<Button>(R.id.teacher).setOnClickListener(this)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.student -> triggerStudentFlow()
            R.id.teacher -> triggerTeacherFlow()
        }
    }

    private fun triggerTeacherFlow() {
        TODO("Not yet implemented")
    }

    private fun triggerStudentFlow() {
        // Update User Profile Photo to "Student"
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse("Student")
        }
        user!!.updateProfile(profileUpdates).addOnCompleteListener {
            task -> if(task.isSuccessful) {
            Log.d(TAG, "User profile updated.")
        }
        }

        val studentIntent = Intent(this, StudentRegistrationActivity::class.java)
        startActivity(studentIntent)
    }
}