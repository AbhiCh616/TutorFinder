package com.example.tutorfinder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tutorfinder.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    companion object {
        private const val RC_SIGN_IN: Int = 1
        private val TAG = LoginActivity::class.qualifiedName
    }

    // Google sign in
    private var googleSignInClient: GoogleSignInClient? = null

    // Firebase sign in
    private lateinit var auth: FirebaseAuth

    // Views
    private lateinit var emailText: TextView
    private lateinit var emailField: TextInputEditText
    private lateinit var passwordText: TextView
    private lateinit var passwordField: TextInputEditText
    private lateinit var emailLogInButton: MaterialButton
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var goToSignUpText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        // Instantiate views
        emailText = findViewById(R.id.email_text)
        emailField = findViewById(R.id.email_field)
        passwordText = findViewById(R.id.password_text)
        passwordField = findViewById(R.id.password_field)
        emailLogInButton = findViewById(R.id.email_log_in_button)
        googleSignInButton = findViewById(R.id.google_log_in_button)
        goToSignUpText = findViewById(R.id.go_to_sign_up)

        // Set onClick listener
        googleSignInButton.setOnClickListener(this)
        emailLogInButton.setOnClickListener(this)
        goToSignUpText.setOnClickListener(this)

        // Set onFocus listener
        emailField.onFocusChangeListener = this
        passwordField.onFocusChangeListener = this

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
        when (v.id) {
            R.id.google_log_in_button -> signIn()
            R.id.email_log_in_button -> signInUsingEmail()
            R.id.go_to_sign_up -> startSignUpActivity()
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        when(v.id) {
            R.id.email_field -> {
                // When email field is in focus
                if(hasFocus) {
                    emailField.hint = ""
                    emailText.visibility = View.VISIBLE
                }
                // When email field is not in focus
                else {
                    emailField.hint = getString(R.string.email)
                    emailText.visibility = View.INVISIBLE
                }
            }

            R.id.password_field -> {
                // When password field is in focus
                if(hasFocus) {
                    passwordField.hint = ""
                    passwordText.visibility = View.VISIBLE
                }
                // When password field is not in focus
                else {
                    passwordField.hint = getString(R.string.password)
                    passwordText.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun signInUsingEmail() {
        // Hide virtual keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(emailLogInButton.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)

        // If all details are inserted
        if(validateForm()) {
            auth.signInWithEmailAndPassword(emailField.text.toString().trim(), passwordField.text.toString().trim())
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Snackbar.make(emailLogInButton, R.string.authentication_failed_warning, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                            .show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun validateForm(): Boolean {
        // Email field is empty
        if(emailField.text.toString().trim() == "") {
            Snackbar.make(emailField, R.string.email_empty_warning, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                .show()
            return false;
        }
        // Password field is empty
        else if(passwordField.text.toString().trim() == "") {
            Snackbar.make(passwordField, R.string.password_empty_warning, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                .show()
            return false;
        }

        return true;
    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun startSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
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

    /* Get an ID token from the GoogleSignInAccount object,
     exchange it for a Firebase credential,
     and authenticate with Firebase using the Firebase credential */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential: success")
                        val user = auth.currentUser
                        // Check if the user is new
                        val isUserNew: Boolean = task.result!!.additionalUserInfo!!.isNewUser
                        updateUI(user, isUserNew)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential: failure", task.exception)
                        Snackbar.make(googleSignInButton, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

}