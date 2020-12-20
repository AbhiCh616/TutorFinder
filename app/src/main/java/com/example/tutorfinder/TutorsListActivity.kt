package com.example.tutorfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.adapters.TutorInfoBriefAdapter
import com.example.tutorfinder.models.TutorInfoBrief
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class TutorsListActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = TutorsListActivity::class.qualifiedName
    }

    private var googleSignInClient: GoogleSignInClient? = null

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private lateinit var signOutButton: Button
    private lateinit var displayName: TextView

    private val tutorInfoBriefList: Array<TutorInfoBrief> = arrayOf(
            TutorInfoBrief("Suruchi", 3.5F, listOf("Mathematics"), 4000),
            TutorInfoBrief("Achal", 4.5F, listOf("Science"), 1000000)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutors_list)

        signOutButton = findViewById(R.id.sign_out_button)
        displayName = findViewById(R.id.display_name)

        // When sign out button is clicked
        signOutButton.setOnClickListener(this)

        val tutorListRecyclerView: RecyclerView = findViewById(R.id.rv_tutor_list)
        tutorListRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TutorInfoBriefAdapter(tutorInfoBriefList)
        tutorListRecyclerView.adapter = adapter

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
        if (user == null) {
            // If user is logged out, take them to login page
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginActivityIntent)
        } else {
            displayName.text = user.displayName
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_out_button -> signOut()
        }
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient?.signOut()?.addOnCompleteListener(this) {
            updateUI(null)
        }
    }
}