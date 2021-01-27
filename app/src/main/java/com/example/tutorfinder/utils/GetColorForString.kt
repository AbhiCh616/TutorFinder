package com.example.tutorfinder.utils

import com.example.tutorfinder.R
import com.google.firebase.database.collection.LLRBNode

object GetColorForString {

    private const val NUM_COLOUR = 10;

    fun getColor(s: String): Pair<Int, Int> {
        val hash = s.hashCode() % NUM_COLOUR

        return when(hash) {
            in 0..9 -> Pair(R.color.tag_1_back, R.color.tag_1_fore)
            else -> Pair(R.color.tag_2_back, R.color.tag_2_fore)
        }
    }
}