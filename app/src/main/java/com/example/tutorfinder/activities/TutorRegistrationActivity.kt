package com.example.tutorfinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tutorfinder.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TutorRegistrationActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = TutorRegistrationActivity::class.qualifiedName
        private const val PICK_IMAGE_REQUEST = 1
    }

    // Views
    private lateinit var profilePicView: ImageView
    private lateinit var nameField: EditText
    private lateinit var rateField: EditText
    private lateinit var subjectsField: EditText
    private lateinit var aboutMe: EditText
    private lateinit var distanceDisplay: TextView
    private lateinit var distanceSeekBar: SeekBar
    private lateinit var submitButton: Button

    // Firebase authentication
    private var user: FirebaseUser = Firebase.auth.currentUser!!

    // Firebase firestore
    private val db = Firebase.firestore

    // Firebase cloud storage
    private val storageReference = Firebase.storage.reference

    private var profilePicUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration)

        // Instantiate views
        profilePicView = findViewById(R.id.profile_pic)
        nameField = findViewById(R.id.name)
        rateField = findViewById(R.id.rate)
        subjectsField = findViewById(R.id.subjects)
        aboutMe = findViewById(R.id.about_me)
        distanceDisplay = findViewById(R.id.distance_display)
        distanceSeekBar = findViewById(R.id.seekBar)
        submitButton = findViewById(R.id.submit_button)

        // Set onClick listener
        profilePicView.setOnClickListener(this)
        submitButton.setOnClickListener(this)

        // Handle distance slider
        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                distanceDisplay.text = ((progress / 10).toString() + " " + getString(R.string.km))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_pic -> selectImage()
            R.id.submit_button -> submit()
        }
    }

    // Pick up image from gallery
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If result is of picking image request
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.data != null) {
            profilePicUri = data.data

            // Set profile pic on screen to selected image
            Glide.with(this)
                    .load(profilePicUri)
                    .into(profilePicView)
        }
    }

    private fun submit() {

        // Upload image to firebase cloud storage
        val profilePicReference = storageReference.child(user.uid)
        profilePicReference.putFile(profilePicUri!!)
                .addOnSuccessListener {

                    // Set data to create document
                    val subjectsList = listOf<String>(subjectsField.text.toString())
                    val newUserInfo = hashMapOf(
                            "profilePic" to "gs://tutor-finder-f8d8d.appspot.com/" + user.uid,
                            "name" to nameField.text.toString(),
                            "rating" to 0,
                            "subjects" to subjectsList,
                            "rate" to rateField.text.toString().toInt(),
                            "aboutMe" to aboutMe.text.toString()
                    )

                    // Create document in firestore tutors collection
                    db.collection("tutors")
                            .document(user.uid)
                            .set(newUserInfo)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }

                    openNextActivity()
                }
    }

    // Start the tutor activity and clear all previous activities from stack
    private fun openNextActivity() {
        val intent = Intent(this, TutorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}