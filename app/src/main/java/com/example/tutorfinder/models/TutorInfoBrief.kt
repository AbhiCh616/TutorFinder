package com.example.tutorfinder.models

import android.net.Uri

data class TutorInfoBrief(
        val name : String,
        val rating: Float,
        val subjects: List<String>,
        val rate: Int,
        val profilePic : Uri = Uri.EMPTY
)