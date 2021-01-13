package com.example.tutorfinder.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.R
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

class SignUpActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val RC_SIGN_IN: Int = 1
        private val TAG = SignUpActivity::class.qualifiedName
    }

    // Google sign in
    private var googleSignInClient: GoogleSignInClient? = null

    // Firebase sign in
    private lateinit var auth: FirebaseAuth

    // Views
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var googleSignInButton: SignInButton
    private lateinit var emailSignUpButton: Button
    private lateinit var goToLogInText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_screen)

        // Instantiate views
        email = findViewById(R.id.email_field)
        password = findViewById(R.id.password_field)
        googleSignInButton = findViewById(R.id.google_sign_up_button)
        emailSignUpButton = findViewById(R.id.email_sign_up_button)
        goToLogInText = findViewById(R.id.go_to_log_in_text)

        // Set onClick listener
        googleSignInButton.setOnClickListener(this)
        emailSignUpButton.setOnClickListener(this)
        goToLogInText.setOnClickListener(this)

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

        /* Check for existing Google Sign In account, if the user is already signed in
         the GoogleSignInAccount will be non-null. */
        val user = auth.currentUser
        updateUI(user)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.google_sign_up_button -> googleSignIn()
            R.id.email_sign_up_button -> emailSignUp()
            R.id.go_to_log_in_text -> startLogInActivity()
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, SignUpActivity.RC_SIGN_IN)
    }

    private fun emailSignUp() {
        if(validateForm()) {
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
        }
    }

    private fun validateForm(): Boolean {
        return true
    }

    private fun startLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun updateUI(user: FirebaseUser?, isNewUser: Boolean = false) {
        if (user != null) {
            val intent = when {
                // If the user is new
                isNewUser -> Intent(this, SelectRoleActivity::class.java)
                // If the user is not new and a student
                user.photoUrl.toString() == "student" -> Intent(this, StudentActivity::class.java)
                // If the user is not new and a teacher
                else -> Intent(this, TutorActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SignUpActivity.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(SignUpActivity.TAG, "firebaseAuthWithGoogle: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(SignUpActivity.TAG, "Google sign in failed", e)
            }
        }
    }

    /* Get an ID token from the GoogleSignInAccount object,
     exchange it for a Firebase credential,
     and authenticate with Firebase using the Firebase credential */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(SignUpActivity.TAG, "signInWithCredential: success")
                        val user = auth.currentUser
                        // Check if the user is new
                        val isUserNew: Boolean = task.result!!.additionalUserInfo!!.isNewUser
                        updateUI(user, isUserNew)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(SignUpActivity.TAG, "signInWithCredential: failure", task.exception)
                        Snackbar.make(googleSignInButton, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

}