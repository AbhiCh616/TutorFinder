package com.example.tutorfinder.interfaces

import com.example.tutorfinder.utils.PerCostFactor

interface SubjectListener {
    fun setSubject(subjects: List<String>)
    fun setCost(cost: Int)
    fun setCostFactor(perCostFactor: PerCostFactor)
}