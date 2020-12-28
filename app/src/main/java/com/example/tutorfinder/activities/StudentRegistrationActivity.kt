package com.example.tutorfinder.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentRegistrationActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = StudentRegistrationActivity::class.qualifiedName
    }

    // Firebase authentication
    private val user: FirebaseUser = Firebase.auth.currentUser!!

    // Firebase firestore
    private val db = Firebase.firestore

    // Views
    private lateinit var nameField: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_registration)

        // View Instantiated
        nameField = findViewById(R.id.name)
        submitButton = findViewById(R.id.submit)

        // Set on click listeners
        submitButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.submit -> submitInfo()
        }
    }

    // Save user profile data in Firestore
    private fun submitInfo() {
        val newUserInfo = hashMapOf<String, String>("name" to nameField.text.toString())
        db.collection("students")
                .document(user.uid).set(newUserInfo)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added.")
                    openNextActivity()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }

    // Start the student activity and clear all previous activities from stack
    private fun openNextActivity() {
        val intent = Intent(this, StudentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}