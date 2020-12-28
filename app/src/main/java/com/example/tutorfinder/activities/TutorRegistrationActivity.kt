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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TutorRegistrationActivity: AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    companion object {
        private val TAG = TutorRegistrationActivity::class.qualifiedName
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var profilePicView: ImageView
    private lateinit var nameField: EditText
    private lateinit var rateField: EditText
    private lateinit var subjectsField: EditText
    private lateinit var distanceDisplay: TextView
    private lateinit var distanceSeekBar: SeekBar
    private lateinit var submitButton: Button

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private val db = Firebase.firestore

    private var profilePicUri: Uri? = null

    private val storage = Firebase.storage
    private val storageReference = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration)

        profilePicView = findViewById(R.id.profile_pic)
        nameField = findViewById(R.id.name)
        rateField = findViewById(R.id.rate)
        subjectsField = findViewById(R.id.subjects)
        distanceDisplay = findViewById(R.id.distance_display)
        distanceSeekBar = findViewById(R.id.seekBar)
        submitButton = findViewById(R.id.submit_button)

        profilePicView.setOnClickListener(this)
        submitButton.setOnClickListener(this)
        distanceSeekBar.setOnSeekBarChangeListener(this)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.profile_pic -> selectImage()
            R.id.submit_button -> submit()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun submit() {

        // Upload image to firebase cloud storage
        val profilePicReference = storageReference.child(user!!.uid)
        profilePicReference.putFile(profilePicUri!!)
                .addOnSuccessListener {
                    // Set data to upload
                    val subjectsList = listOf<String>(subjectsField.text.toString())
                    val newUserInfo = hashMapOf(
                            "profilePic" to "gs://tutor-finder-f8d8d.appspot.com/" + user!!.uid,
                            "name" to nameField.text.toString(),
                            "rating" to 0,
                            "subjects" to subjectsList,
                            "rate" to rateField.text.toString().toInt(),
                    )

                    // Upload to firebase
                    db.collection("tutors")
                            .document(user!!.uid)
                            .set(newUserInfo)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.data != null) {
            profilePicUri = data.data

            Glide.with(this)
                    .load(profilePicUri)
                    .into(profilePicView)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        distanceDisplay.text = (progress/10).toString() + " " + getString(R.string.km)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}