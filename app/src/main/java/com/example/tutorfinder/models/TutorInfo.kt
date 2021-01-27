package com.example.tutorfinder.models

import android.net.Uri

data class TutorInfo(
        val name: String = "",
        val rating: Float = 0f,
        val subjects: List<String> = listOf(""),
        val cost: Int = 0,
        val profilePic: String = "",
        val about: String = "",
        val costFactor: String = "",
        val age: Int = 0,
        val educationDetails: String = "",
        val experienceDetails: String = "",
        val gender: String = "",
)