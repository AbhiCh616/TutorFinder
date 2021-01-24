package com.example.tutorfinder.interfaces

import com.example.tutorfinder.utils.Gender

interface BasicInfoListener {
    fun setName(name: String?)
    fun setAge(age: Int?)
    fun setGender(gender: Gender?)
}