package com.example.tutorfinder.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tutorfinder.R
import com.example.tutorfinder.utils.Gender
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class StudentRegistrationActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = StudentRegistrationActivity::class.qualifiedName
    }

    // Firebase authentication
    private val user: FirebaseUser = Firebase.auth.currentUser!!

    // Firebase firestore
    private val db = Firebase.firestore

    // Views
    private lateinit var name: TextInputEditText
    private lateinit var age: TextInputEditText
    private lateinit var genderMaleButton: MaterialButton
    private lateinit var genderFemaleButton: MaterialButton
    private lateinit var genderOtherButton: MaterialButton
    private lateinit var nextButton: ShapeableImageView

    // To store user input
    private var gender: Gender? = null

    // Firebase cloud storage
    private val storageReference = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_registration)

        // View Instantiated
        name = findViewById(R.id.name)
        age = findViewById(R.id.age)
        genderMaleButton = findViewById(R.id.gender_male_button)
        genderFemaleButton = findViewById(R.id.gender_female_button)
        genderOtherButton = findViewById(R.id.gender_other_button)
        nextButton = findViewById(R.id.next_button)

        // Set on click listeners
        nextButton.setOnClickListener(this)
        genderMaleButton.setOnClickListener(this)
        genderFemaleButton.setOnClickListener(this)
        genderOtherButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        when (gender) {

            Gender.Male -> selectGenderMale()
            Gender.Female -> selectGenderFemale()
            Gender.Other -> selectGenderOther()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.gender_male_button -> selectGenderMale()
            R.id.gender_female_button -> selectGenderFemale()
            R.id.gender_other_button -> selectGenderOther()
            R.id.next_button -> submitInfo()
        }
    }

    // To validate all the fields
    fun validateForm(): Boolean {
        val nameValue = name.text.toString()
        val ageValue = age.text.toString()
        val genderValue = gender

        // Name field is empty
        if (nameValue.trim() == "") {
            Snackbar.make(findViewById(R.id.next_button),
                    getString(R.string.name_empty_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        // Age field is empty
        if (ageValue.trim() == "") {
            Snackbar.make(findViewById(R.id.next_button),
                    getString(R.string.age_empty_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        // Gender is not selected
        if (genderValue == null) {
            Snackbar.make(findViewById(R.id.next_button),
                    getString(R.string.gender_empty_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                    .show()
            return false
        }
        return true
    }

    // Save user profile data in Firestore
    private fun submitInfo() {
        if(validateForm()) {
            // Set data to create document
            val newUserInfo = hashMapOf(
                    "name" to name.text.toString(),
                    "age" to age.text.toString().toInt(),
                    "gender" to gender.toString()
            )

            // Create document in firestore tutors collection
            db.collection("students")
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

    // Start the student activity and clear all previous activities from stack
    private fun openNextActivity() {
        val intent = Intent(this, StudentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun selectGenderMale() {
        // Set male button as selected and other gender buttons as unselected
        genderMaleButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.dark_blue))
        genderFemaleButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey_500))
        genderOtherButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey_500))

        gender = Gender.Male
    }

    private fun selectGenderFemale() {
        // Set female button as selected and other gender buttons as unselected
        genderMaleButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey_500))
        genderFemaleButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.dark_blue))
        genderOtherButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey_500))

        gender = Gender.Female
    }

    private fun selectGenderOther() {
        // Set "other" button as selected and other gender buttons as unselected
        genderMaleButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey_500))
        genderFemaleButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey_500))
        genderOtherButton.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.dark_blue))

        gender = Gender.Other
    }
}