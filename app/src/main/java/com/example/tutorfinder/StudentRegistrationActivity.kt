package com.example.tutorfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentRegistrationActivity : AppCompatActivity(), View.OnClickListener
{

    companion object {
        private val TAG = StudentRegistrationActivity::class.qualifiedName
    }

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private lateinit var nameField : EditText
    private lateinit var submitButton: Button

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_registration)

        nameField = findViewById(R.id.name)
        submitButton = findViewById(R.id.submit)

        submitButton.setOnClickListener(this)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser

        //Set name field to what we get from Google account
        nameField.setText(user?.displayName)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.submit -> submitInfo()
        }
    }

    private fun submitInfo() {
        val newUserInfo = hashMapOf<String, String>("name" to nameField.text.toString())
        db.collection("students")
                .document(user!!.uid).set(newUserInfo)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added.")
                    openNextActivity()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }

    private fun openNextActivity() {
        val intent = Intent(this, TutorsListActivity::class.java)
        startActivity(intent)
    }
}