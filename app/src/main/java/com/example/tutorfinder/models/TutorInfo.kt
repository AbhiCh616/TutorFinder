package com.example.tutorfinder.models

import android.net.Uri

data class TutorInfo(
        val name: String = "",
        val rating: Float = 0f,
        val subjects: List<String> = listOf(""),
        val rate: Int = 0,
        val profilePic: String = "",
        val aboutMe: String = ""
)