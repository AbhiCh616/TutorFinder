package com.example.tutorfinder.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.example.tutorfinder.fragments.TutorRegBasicInfo
import com.example.tutorfinder.fragments.TutorRegLocation
import com.example.tutorfinder.fragments.TutorRegOptionalFragment
import com.example.tutorfinder.fragments.TutorRegSubject
import com.example.tutorfinder.interfaces.*
import com.example.tutorfinder.utils.Gender
import com.example.tutorfinder.utils.PerCostFactor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class TutorRegistration : AppCompatActivity(), View.OnClickListener,
    BasicInfoListener, DistanceListener, SubjectListener, TutorRegOptionalListener {

    companion object {
        private val TAG = TutorRegistration::class.qualifiedName
        private const val FINE_LOCATION_PERMISSION_REQUEST = 1
    }

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Manage Fragments
    private var presentFragmentNumber: Int = 0
    private val fragmentList = listOf<Fragment>(
        TutorRegBasicInfo(), TutorRegLocation(),
        TutorRegSubject(), TutorRegOptionalFragment()
    )

    // Views
    private lateinit var nextButton: ShapeableImageView

    // User info
    private var profilePicUri: Uri? = null
    private var name: String? = null
    private var age: Int? = null
    private var gender: Gender? = null
    private var distance: Float? = null
    private var subjects: List<String>? = null
    private var cost: Int? = null
    private var perCostFactor: PerCostFactor? = null
    private var aboutMe: String? = null
    private var education: String? = null
    private var experience: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    // Firebase authentication
    private var user: FirebaseUser = Firebase.auth.currentUser!!

    // Firebase firestore
    private val db = Firebase.firestore

    // Firebase cloud storage
    private val storageReference = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutor_registration_activity)

        // Initialize views
        nextButton = findViewById(R.id.next_button)

        // Set on click listener
        nextButton.setOnClickListener(this)

        // Select first fragment to appear
        startFragment(0)

        // Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_button -> changeFragment()
        }
    }

    private fun changeFragment() {
        // Get data from fragment
        if (fragmentList[presentFragmentNumber] is SetAllEntries) {
            val fragment = fragmentList[presentFragmentNumber] as SetAllEntries
            if (fragment.validateForm()) {
                fragment.setAllEntries()
            } else {
                return
            }
        } else {
            Log.e(TAG, TAG + " is not of type " + SetAllEntries::class.qualifiedName)
        }



        // If we are not at the last fragment of the list
        if (++presentFragmentNumber < fragmentList.size) {
            startFragment(presentFragmentNumber)

            // If on location page, get location permission
            if (fragmentList[presentFragmentNumber] is TutorRegLocation) {
                getLocationPermission()
            }
            // If on last page, set location
            if(presentFragmentNumber == fragmentList.size - 1) {
                setLocation()
            }

            Log.d(
                    TAG, profilePicUri.toString() + " " + name + " " + age + " "
                    + gender.toString() + " " + distance + " " + subjects.toString()
                    + " " + cost + " " + perCostFactor.toString() + " " + aboutMe + " "
                    + education + " " + experience + " " + latitude + " " + longitude
            )

        } else {
            registerTutor()
        }

    }

    private fun registerTutor() {
        // Upload image to firebase cloud storage
        val profilePicReference = storageReference.child(user.uid)
        profilePicReference.putFile(profilePicUri!!)
                .addOnSuccessListener {

                    // Set data to create document
                    val newUserInfo = hashMapOf(
                            "profilePic" to "gs://tutor-finder-f8d8d.appspot.com/" + user.uid,
                            "name" to name,
                            "age" to age,
                            "gender" to gender.toString(),
                            "latitude" to latitude,
                            "longitude" to longitude,
                            "distance" to distance,
                            "subjects" to subjects,
                            "cost" to cost,
                            "costFactor" to perCostFactor,
                            "about" to aboutMe,
                            "educationDetails" to education,
                            "experienceDetails" to experience
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

    // Show the fragment at the given number of the list
    private fun startFragment(i: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentList[i])
            .commit()
    }

    override fun onBackPressed() {
        // If we are on the first fragment, go to previous activity
        if (presentFragmentNumber == 0) {
            super.onBackPressed()
        }
        // Go to previous fragment
        else {
            startFragment(--presentFragmentNumber)
        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Ask for location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_PERMISSION_REQUEST
            )

            return
        }
    }

    private fun setLocation() {
        // If location permission not granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            getLocationPermission()

            return
        } else {
            // Set location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    // Can't get location
                    if (location == null) {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.cant_get_location),
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.red))
                            .show()
                    }

                    latitude = location?.latitude
                    longitude = location?.longitude
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FINE_LOCATION_PERMISSION_REQUEST -> {
                // If request is cancelled, result array is empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation()
                }
            }
        }
    }

    override fun setProfilePicUri(profilePicUri: Uri?) {
        this.profilePicUri = profilePicUri
    }

    override fun setName(name: String?) {
        this.name = name
    }

    override fun setAge(age: Int?) {
        this.age = age
    }

    override fun setGender(gender: Gender?) {
        this.gender = gender
    }

    override fun setDistance(distance: Float?) {
        this.distance = distance
    }

    override fun setSubject(subjects: List<String>) {
        this.subjects = subjects
    }

    override fun setCost(cost: Int) {
        this.cost = cost
    }

    override fun setCostFactor(perCostFactor: PerCostFactor) {
        this.perCostFactor = perCostFactor
    }

    override fun setAbout(about: String) {
        this.aboutMe = about
    }

    override fun setEducation(education: String) {
        this.education = education
    }

    override fun setExperience(experience: String) {
        this.experience = experience
    }

}