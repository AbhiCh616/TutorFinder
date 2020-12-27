package com.example.tutorfinder

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.models.TutorInfo
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class TutorDetailsActivity: AppCompatActivity() {

    companion object {
        private val TAG = TutorDetailsActivity::class.qualifiedName
    }

    private lateinit var docRefString: String

    private val db = Firebase.firestore
    private lateinit var docRef: DocumentReference

    private var tutorInfo: TutorInfo? = null

    private lateinit var name: TextView
    private lateinit var profilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_details)

        profilePic = findViewById(R.id.profile_pic)
        name = findViewById(R.id.name)

        // Get firestore document reference
        docRefString = intent.getStringExtra("docRef").toString()
        docRef = db.collection("tutors").document(docRefString)

        // Store document from firebase in object
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                tutorInfo = documentSnapshot.toObject<TutorInfo>()

                // Display name
                name.text = tutorInfo?.name

                //Display image
                val storage = FirebaseStorage.getInstance()
                val gsReference = storage.getReferenceFromUrl(tutorInfo!!.profilePic)
                GlideApp.with(profilePic.context)
                    .load(gsReference)
                    .into(profilePic)
            }
    }
}