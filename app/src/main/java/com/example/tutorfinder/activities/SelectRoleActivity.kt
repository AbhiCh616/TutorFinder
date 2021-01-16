package com.example.tutorfinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class SelectRoleActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = SelectRoleActivity::class.qualifiedName
    }

    // Google sign in
    private var googleSignInClient: GoogleSignInClient? = null

    // Firebase authentication
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    // Views
    private lateinit var selectStudentButton: MaterialButton
    private lateinit var selectTeacherButton: MaterialButton
    private lateinit var name: MaterialTextView
    private lateinit var signOutText: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_role)

        // Authentication
        auth = Firebase.auth

        // Instantiate views
        selectStudentButton = findViewById(R.id.student)
        selectTeacherButton = findViewById(R.id.teacher)
        name = findViewById(R.id.name)
        signOutText = findViewById(R.id.sign_out_text)

        // set onClick listener
        selectStudentButton.setOnClickListener(this)
        selectTeacherButton.setOnClickListener(this)
        signOutText.setOnClickListener(this)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()

        // Get logged in user
        user = auth.currentUser!!

        // Display name of the user
        name.text = (" " + user.displayName + "?")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.student -> triggerStudentRegistration()
            R.id.teacher -> triggerTutorRegistration()
            R.id.sign_out_text -> signOut()
        }
    }

    // If the new user is student
    private fun triggerStudentRegistration() {
        // Update user profile photo to "student"
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse("student")
        }
        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Student image updated.")
            }
        }

        // Start student registration activity
        val studentIntent = Intent(this, StudentRegistrationActivity::class.java)
        startActivity(studentIntent)
    }

    // If the new user is tutor
    private fun triggerTutorRegistration() {
        // Start tutor registration activity
        val tutorIntent = Intent(this, TutorRegistrationActivity::class.java)
        startActivity(tutorIntent)
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient?.signOut()?.addOnCompleteListener(this) {

            // Start login activity and delete the track
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}