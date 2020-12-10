package com.example.tutorfinder

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.adapters.TutorInfoBriefAdapter
import com.example.tutorfinder.models.TutorInfoBrief

class MainActivity : AppCompatActivity() {

    val tutorInfoBriefList : Array<TutorInfoBrief> = arrayOf(
        TutorInfoBrief("Suruchi", 3.5F, listOf("Mathematics"), 4000),
        TutorInfoBrief("Achal", 4.5F, listOf("Science"), 1000000)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tutorListRecyclerView : RecyclerView = findViewById(R.id.rv_tutor_list)
        val adapter = TutorInfoBriefAdapter(tutorInfoBriefList)
        tutorListRecyclerView.adapter = adapter
        tutorListRecyclerView.layoutManager = LinearLayoutManager(this)

    }
}