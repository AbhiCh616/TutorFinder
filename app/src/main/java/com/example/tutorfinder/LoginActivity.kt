package com.example.tutorfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val RC_SIGN_IN: Int = 1
        private val TAG = LoginActivity::class.qualifiedName
    }

    private var googleSignInClient: GoogleSignInClient? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var signInButton: SignInButton
    private lateinit var signOutButton: Button
    private lateinit var displayName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        signInButton = findViewById(R.id.sign_in_button)
        signOutButton = findViewById(R.id.sign_out_button)
        displayName = findViewById(R.id.display_name)

        // Set on click listener
        signInButton.setOnClickListener(this)
        signOutButton.setOnClickListener(this)

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

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val user = auth.currentUser
        updateUI(user)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient?.signOut()?.addOnCompleteListener(this) {
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?, isNewUser: Boolean = false) {
        if (user != null) {
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
            displayName.text = user.displayName

            // If the user is new
            if (isNewUser) {
                val roleSelectionActivity = Intent(this, SelectRoleActivity::class.java)
                startActivity(roleSelectionActivity)
            }
            // If the user is not new and is a student
            else if (user.photoUrl.toString() == "Student") {
                val tutorsListActivity = Intent(this, TutorsListActivity::class.java)
                startActivity(tutorsListActivity)
            }
            // If the user is not new and a teacher
            else {
                val tutorProfileActivity = Intent(this, TutorProfileActivity::class.java)
                startActivity(tutorProfileActivity)
            }
        }

        // If the user is not signed in
        else {
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // Get an ID token from the GoogleSignInAccount object,
    // exchange it for a Firebase credential,
    // and authenticate with Firebase using the Firebase credential
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential: success")
                        // Check if the user is new
                        val isUserNew: Boolean = task.result!!.additionalUserInfo!!.isNewUser
                        val user = auth.currentUser
                        updateUI(user, isUserNew)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential: failure", task.exception)
                        Snackbar.make(signInButton, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }
}