package com.example.tutorfinder.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.example.tutorfinder.activities.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StudentProfileFragment : Fragment(), View.OnClickListener {

    companion object {
        private val TAG = StudentProfileFragment::class.qualifiedName
    }

    // Google sign in
    private var googleSignInClient: GoogleSignInClient? = null

    // Firebase auth
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    // Views
    private lateinit var signOutButton: Button
    private lateinit var deleteAccountButton: Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

        return inflater.inflate(R.layout.student_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        signOutButton = requireView().findViewById(R.id.sign_out_button)
        deleteAccountButton = requireView().findViewById(R.id.delete_account_button)

        // Set onClick listener
        signOutButton.setOnClickListener(this)
        deleteAccountButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        // Get current user
        currentUser = auth.currentUser!!
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_out_button -> signOut()
            R.id.delete_account_button -> deleteAccount()
        }
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient?.signOut()?.addOnCompleteListener(requireActivity()) {

            // Start login activity and delete the track
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun deleteAccount() {

        // Delete user from firebase
        currentUser.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User account deleted.")

                        // Sign out user
                        signOut()
                    }
                }
    }

}