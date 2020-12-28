package com.example.tutorfinder.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity: AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    // Google sign in
    private var googleSignInClient: GoogleSignInClient? = null

    // Firebase sign in
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        var intent: Intent
        // If user is logged out
        if(user == null) {
            intent = Intent(this, LoginActivity::class.java)
        }
        else {
            // If user is student
            if(user.photoUrl.toString() == "student") {
                intent = Intent(this, StudentActivity::class.java)
            }
            // If user is tutor
            else {
                intent = Intent(this, TutorActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
    }

}