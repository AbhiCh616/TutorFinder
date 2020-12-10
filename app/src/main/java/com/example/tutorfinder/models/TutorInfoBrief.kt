package com.example.tutorfinder.models

import android.net.Uri

data class TutorInfoBrief(
        val profilePic : Uri,
        val name : String,
        val rating: Int,
        val subjects: List<String>,
        val rate: Int
)