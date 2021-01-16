package com.example.tutorfinder.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    // Firebase sign in
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    // Firebase Firestore
    private lateinit var rootRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Firebase Firestore
        rootRef = FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val docRefInStudent = rootRef.collection("students").document(user.uid)
            docRefInStudent.get()
                    .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                        if(task.isSuccessful) {
                            val doc = task.result
                            if(doc!!.exists()) {
                                // User is already registered student
                                val intent = Intent(this, StudentActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // User is not student
                                // Check if the document with id of user uid exists in tutors collection
                                // i.e., the user is already registered tutor
                                val docRefInTutor = rootRef.collection("tutors").document(user.uid)
                                docRefInTutor.get()
                                        .addOnCompleteListener { task2: Task<DocumentSnapshot> ->
                                            if (task2.isSuccessful) {
                                                val doc2 = task2.result
                                                if (doc2!!.exists()) {
                                                    // User is already registered tutor
                                                    val intent = Intent(this, TutorActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    // User is not a registered tutor, it is a new user
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