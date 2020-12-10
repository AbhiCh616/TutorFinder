package com.example.tutorfinder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.R
import com.example.tutorfinder.models.TutorInfoBrief

class TutorInfoBriefAdapter(private val tutorInfoSet : Array<TutorInfoBrief>) :
    RecyclerView.Adapter<TutorInfoBriefAdapter.TutorInfoBriefViewHolder>() {

    inner class TutorInfoBriefViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val name : TextView = view.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorInfoBriefViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tutor_info_brief_card, parent, false)
        return TutorInfoBriefViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorInfoBriefViewHolder, position: Int) {
        holder.apply {
            name.text = tutorInfoSet[position].name
        }
    }

    override fun getItemCount() = tutorInfoSet.size
}