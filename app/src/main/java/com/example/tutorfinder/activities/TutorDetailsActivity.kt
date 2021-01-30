package com.example.tutorfinder.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tutorfinder.utils.GlideApp
import com.example.tutorfinder.R
import com.example.tutorfinder.models.TutorInfo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

class TutorDetailsActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = TutorDetailsActivity::class.qualifiedName
    }

    // Firebase firestore
    private val db = Firebase.firestore

    // Document name inside tutors collection to open
    private lateinit var docRefString: String

    // Reference of document to open
    private lateinit var docRef: DocumentReference

    // Firebase authentication
    private var user: FirebaseUser = Firebase.auth.currentUser!!

    // Views
    private lateinit var name: TextView
    private lateinit var profilePic: ImageView
    private lateinit var aboutMe: TextView
    private lateinit var writeReview: TextView
    private lateinit var rating: TextView
    private lateinit var educationField: TextView
    private lateinit var experienceField: TextView
    private lateinit var educationHeading: TextView
    private lateinit var experienceHeading: TextView

    // Used by review dialog box
    var starSelected = 0

    // To store firestore document
    private var tutorInfo: TutorInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_details)

        // Instantiate views
        profilePic = findViewById(R.id.profile_pic)
        name = findViewById(R.id.name)
        aboutMe = findViewById(R.id.about_me)
        writeReview = findViewById(R.id.write_review)
        rating = findViewById(R.id.rating)
        educationField = findViewById(R.id.education_field)
        experienceField = findViewById(R.id.experience_field)
        educationHeading = findViewById(R.id.educationHeading)
        experienceHeading = findViewById(R.id.experienceHeading)

        // Set on click listeners
        writeReview.setOnClickListener(this)

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
                    if (tutorInfo?.about != "") {
                        aboutMe.text = tutorInfo?.about
                    } else {
                        aboutMe.visibility = View.GONE
                    }

                    // Display rating
                    rating.text = tutorInfo?.rating.toString()

                    // Display education
                    if (tutorInfo?.educationDetails != "") {
                        educationField.text = tutorInfo?.educationDetails
                    } else {
                        educationField.visibility = View.GONE
                        educationHeading.visibility = View.GONE
                    }

                    //Display experience
                    if (tutorInfo?.experienceDetails != "") {
                        experienceField.text = tutorInfo?.experienceDetails
                    } else {
                        experienceField.visibility = View.GONE
                        experienceHeading.visibility = View.GONE
                    }

                    //Display image
                    val storage = FirebaseStorage.getInstance()
                    val gsReference = storage.getReferenceFromUrl(tutorInfo!!.profilePic)
                    GlideApp.with(profilePic.context)
                            .load(gsReference)
                            .circleCrop()
                            .into(profilePic)
                }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.write_review -> getReview()
        }
    }

    private fun getReview() {
        // Create dialog box to ask for review
        val builder = AlertDialog.Builder(this)

        // Set dialog prompt title
        builder.setTitle(R.string.add_review)

        // Inflate layout for content of dialog box
        val inflatedView = LayoutInflater.from(this)
                .inflate(R.layout.add_review_prompt, null)

        // Get all views of inflated view
        val star1 = inflatedView.findViewById<ImageView>(R.id.star1)
        val star2 = inflatedView.findViewById<ImageView>(R.id.star2)
        val star3 = inflatedView.findViewById<ImageView>(R.id.star3)
        val star4 = inflatedView.findViewById<ImageView>(R.id.star4)
        val star5 = inflatedView.findViewById<ImageView>(R.id.star5)
        val description = inflatedView.findViewById<EditText>(R.id.description)

        // Handle clicks on star
        val stars = listOf<ImageView>(star1, star2, star3, star4, star5)
        star1.setOnClickListener {
            starClickHandler(stars, 1)
        }
        star2.setOnClickListener {
            starClickHandler(stars, 2)
        }
        star3.setOnClickListener {
            starClickHandler(stars, 3)
        }
        star4.setOnClickListener {
            starClickHandler(stars, 4)
        }
        star5.setOnClickListener {
            starClickHandler(stars, 5)
        }

        // Create dialog
        val dialog = builder.setView(inflatedView)
                .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                    val newReview = hashMapOf(
                            "stars" to starSelected,
                            "description" to description.text.toString()
                    )

                    db.collection("tutors")
                            .document(docRefString)
                            .collection("ratings")
                            .document(user.uid)
                            .set(newReview)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                }
                .setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, _: Int ->
                    starSelected = 0
                    dialogInterface.cancel()
                }
                .create()

        dialog.show()
    }

    // Helper function for review dialog
    fun starClickHandler(stars: List<ImageView>, starNumber: Int) {
        starSelected = starNumber
        // Select stars
        for(i in 0 until starNumber) {
            stars[i].setImageResource(R.drawable.star_filled_24)
            stars[i].imageTintList = ContextCompat.getColorStateList(this, R.color.yellow)
        }
        // Unselect stars
        for(i in starNumber until 5) {
            stars[i].setImageResource(R.drawable.star_outline_24)
            stars[i].imageTintList = ContextCompat.getColorStateList(this, R.color.black)
        }
    }
}