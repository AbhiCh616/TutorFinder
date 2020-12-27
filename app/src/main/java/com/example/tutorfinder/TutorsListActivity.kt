package com.example.tutorfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.adapters.TutorBriefInfoAdapter
import com.example.tutorfinder.models.TutorInfo
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TutorsListActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = TutorsListActivity::class.qualifiedName
    }

    private var googleSignInClient: GoogleSignInClient? = null

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val tutorsRef: CollectionReference = db.collection("tutors")

    private var adapter: TutorBriefInfoAdapter? = null

    private lateinit var signOutButton: Button
    private lateinit var displayName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutors_list)

        signOutButton = findViewById(R.id.sign_out_button)
        displayName = findViewById(R.id.display_name)

        // When sign out button is clicked
        signOutButton.setOnClickListener(this)

        val query: Query = tutorsRef.orderBy("name", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<TutorInfo> = FirestoreRecyclerOptions.Builder<TutorInfo>()
            .setQuery(query, TutorInfo::class.java)
            .build()

        // Set up RecyclerView
        val tutorListRecyclerView: RecyclerView = findViewById(R.id.rv_tutor_list)
        tutorListRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TutorBriefInfoAdapter(options) { docRef: String -> Unit
            val intent = Intent(this, TutorDetailsActivity::class.java)
            intent.putExtra("docRef", docRef)
            startActivity(intent)
        }
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

        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        adapter?.stopListening()
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