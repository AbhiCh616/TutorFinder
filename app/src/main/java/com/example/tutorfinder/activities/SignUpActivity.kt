package com.example.tutorfinder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    companion object {
        private const val RC_SIGN_IN: Int = 1
        private val TAG = SignUpActivity::class.qualifiedName
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
    private lateinit var emailSignUpButton: MaterialButton
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var goToLogInText: TextView

    // Firebase Firestore
    private lateinit var rootRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_screen)

        // Instantiate views
        emailText = findViewById(R.id.email_text)
        emailField = findViewById(R.id.email_field)
        passwordText = findViewById(R.id.password_text)
        passwordField = findViewById(R.id.password_field)
        emailSignUpButton = findViewById(R.id.email_sign_up_button)
        googleSignInButton = findViewById(R.id.google_sign_up_button)
        goToLogInText = findViewById(R.id.go_to_log_in_text)

        // Set onClick listener
        emailSignUpButton.setOnClickListener(this)
        googleSignInButton.setOnClickListener(this)
        goToLogInText.setOnClickListener(this)

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

        // Firebase Firestore
        rootRef = FirebaseFirestore.getInstance()
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
            R.id.google_sign_up_button -> googleSignIn()
            R.id.email_sign_up_button -> emailSignUp()
            R.id.go_to_log_in_text -> startLogInActivity()
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        when (v.id) {
            R.id.email_field -> {
                // When email field is in focus
                if (hasFocus) {
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
                if (hasFocus) {
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

    private fun emailSignUp() {
        // Hide virtual keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(emailSignUpButton.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)

        if (validateForm()) {
            auth.createUserWithEmailAndPassword(emailField.text.toString().trim(), passwordField.text.toString().trim())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            updateUI(user, isNewUser = true)
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
        val emailEntered = emailField.text.toString().trim()
        val passwordEntered = passwordField.text.toString().trim()

        // Email field is empty
        if (emailEntered == "") {
            Snackbar.make(emailField, R.string.email_empty_warning, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        // Email format is wrong
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches()) {
            Snackbar.make(emailField, R.string.email_format_warning, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        // Password field is empty
        if (passwordEntered == "") {
            Snackbar.make(passwordField, R.string.password_empty_warning, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        // Password is short
        if (passwordEntered.length < 8) {
            Snackbar.make(passwordField, R.string.password_short_warning, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        // Password is secure
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&()+\\-*\\\\=_~]).{8,}$"
        if(!Pattern.compile(passwordRegex).matcher(passwordEntered).matches()) {
            Snackbar.make(passwordField, R.string.password_format_warning, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }

        return true
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential: failure", task.exception)
                        Snackbar.make(googleSignInButton, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

    private fun updateUI(user: FirebaseUser?, isNewUser: Boolean = false) {
        if (user != null) {
            if(isNewUser) {
                val intent = Intent(this, SelectRoleActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val docRefInStudent = rootRef.collection("students").document(user.uid)
                docRefInStudent.get()
                        .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                            if (task.isSuccessful) {
                                val doc = task.result
                                if (doc!!.exists()) {
                                    // User is already registered student, launch student activity
                                    val intent = Intent(this, StudentActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // User is not a registered student, check if it is a registered teacher
                                    val docRefInTutor = rootRef.collection("tutors").document(user.uid)
                                    docRefInTutor.get()
                                            .addOnCompleteListener { task2: Task<DocumentSnapshot> ->
                                                if (task2.isSuccessful) {
                                                    val doc2 = task2.result
                                                    if (doc2!!.exists()) {
                                                        // User is already registered tutor
                                                        // take them to tutor activity
                                                        val intent = Intent(this, TutorActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        // User is not a registered tutor, it is a new user
                                                        // start their registration
                                                        val intent = Intent(this, SelectRoleActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }
                                            }
                                }
                            }
                        }
            }
        }
    }

    private fun startLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}