package com.example.tutorfinder.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorfinder.utils.GlideApp
import com.example.tutorfinder.R
import com.example.tutorfinder.models.TutorInfo
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class TutorDetailsActivity : AppCompatActivity() {

    companion object {
        private val TAG = TutorDetailsActivity::class.qualifiedName
    }

    // Firebase firestore
    private val db = Firebase.firestore

    // Document name inside tutors collection to open
    private lateinit var docRefString: String

    // Reference of document to open
    private lateinit var docRef: DocumentReference

    // Views
    private lateinit var name: TextView
    private lateinit var profilePic: ImageView
    private lateinit var aboutMe: TextView
    private lateinit var rating: TextView

    // To store firestore document
    private var tutorInfo: TutorInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_details)

        // Instantiate views
        profilePic = findViewById(R.id.profile_pic)
        name = findViewById(R.id.name)
        aboutMe = findViewById(R.id.about_me)
        rating = findViewById(R.id.rating)

        // Get firestore document reference
        docRefString = intent.getStringExtra("docRef").toString()
        docRef = db.collection("tutors").document(docRefString)

        docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    // Store document from firebase in object
                    tutorInfo = documentSnapshot.toObject<TutorInfo>()

                    // Display name
                    name.text = tutorInfo?.name

                    // Display about me section
                    aboutMe.text = tutorInfo?.aboutMe

                    // Display rating
                    rating.text = tutorInfo?.rating.toString()

                    //Display image
                    val storage = FirebaseStorage.getInstance()
                    val gsReference = storage.getReferenceFromUrl(tutorInfo!!.profilePic)
                    GlideApp.with(profilePic.context)
                            .load(gsReference)
                            .circleCrop()
                            .into(profilePic)
                }
    }
}