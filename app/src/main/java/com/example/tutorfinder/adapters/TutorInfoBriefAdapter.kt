package com.example.tutorfinder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.R
import com.example.tutorfinder.models.TutorInfoBrief

class TutorInfoBriefAdapter(private val tutorInfoBriefSet: Array<TutorInfoBrief>) :
        RecyclerView.Adapter<TutorInfoBriefAdapter.TutorInfoBriefViewHolder>() {

    inner class TutorInfoBriefViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val rating: TextView = view.findViewById(R.id.rating)
        val subjects: TextView = view.findViewById(R.id.subjects)
        val rate: TextView = view.findViewById(R.id.rate)
        //val profilePic : TextView = view.findViewById(R.id.profile_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorInfoBriefViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.tutor_info_brief_card, parent, false)
        return TutorInfoBriefViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorInfoBriefViewHolder, position: Int) {
        holder.apply {
            name.text = tutorInfoBriefSet[position].name
            rating.text = tutorInfoBriefSet[position].rating.toString()
            subjects.text = tutorInfoBriefSet[position].subjects[0]
            rate.text = 8377.toChar() + tutorInfoBriefSet[position].rate.toString()
            //profilePic.text = tutorInfoSet[position].profilePic.toString()
        }
    }

    override fun getItemCount() = tutorInfoBriefSet.size
}