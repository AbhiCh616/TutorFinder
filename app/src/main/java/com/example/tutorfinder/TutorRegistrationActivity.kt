package com.example.tutorfinder

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TutorRegistrationActivity: AppCompatActivity() {

    companion object {
        private val TAG = TutorRegistrationActivity::class.qualifiedName
    }

    private lateinit var nameField: EditText
    private lateinit var rateField: EditText
    private lateinit var subjectsField: EditText

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration)

        nameField = findViewById(R.id.name)
        rateField = findViewById(R.id.rate)
        subjectsField = findViewById(R.id.subjects)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser

        //Set name field to what we get from Google account
        nameField.setText(user?.displayName)
    }
}