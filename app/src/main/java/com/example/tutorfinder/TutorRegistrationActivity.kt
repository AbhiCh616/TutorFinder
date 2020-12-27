package com.example.tutorfinder

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
import com.google.firebase.storage.ktx.storage

class TutorRegistrationActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = TutorRegistrationActivity::class.qualifiedName
    }

    private lateinit var nameField: EditText
    private lateinit var rateField: EditText
    private lateinit var subjectsField: EditText
    private lateinit var submitButton: Button

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration)

        nameField = findViewById(R.id.name)
        rateField = findViewById(R.id.rate)
        subjectsField = findViewById(R.id.subjects)
        submitButton = findViewById(R.id.submit_button)

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
            R.id.submit_button -> submit()
        }
    }

    private fun submit() {
        val subjectsList = listOf<String>(subjectsField.text.toString())
        val newUserInfo = hashMapOf(
            "name" to nameField.text.toString(),
            "rating" to 0,
            "subjects" to subjectsList,
            "rate" to rateField.text.toString().toInt(),
        )

        db.collection("tutors")
            .document(user!!.uid)
            .set(newUserInfo)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}