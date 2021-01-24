package com.example.tutorfinder.interfaces

import android.net.Uri
import com.example.tutorfinder.utils.Gender

interface BasicInfoListener {
    fun setProfilePicUri(profilePicUri: Uri?)
    fun setName(name: String?)
    fun setAge(age: Int?)
    fun setGender(gender: Gender?)
}