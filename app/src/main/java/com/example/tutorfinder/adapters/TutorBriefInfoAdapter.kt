package com.example.tutorfinder.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.GlideApp
import com.example.tutorfinder.R
import com.example.tutorfinder.models.TutorInfoBrief
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage

class TutorBriefInfoAdapter(options: FirestoreRecyclerOptions<TutorInfoBrief>) :
    FirestoreRecyclerAdapter<TutorInfoBrief, TutorBriefInfoAdapter.TutorBriefInfoViewHolder>(options) {

    inner class TutorBriefInfoViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val rating: TextView = view.findViewById(R.id.rating)
        val subjects: TextView = view.findViewById(R.id.subjects)
        val rate: TextView = view.findViewById(R.id.rate)
        val profilePic : ImageView = view.findViewById(R.id.profile_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorBriefInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tutor_info_brief_card, parent, false)
        return TutorBriefInfoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TutorBriefInfoViewHolder,
        position: Int,
        model: TutorInfoBrief
    ) {
        holder.name.text = model.name
        holder.rating.text = "3.5"
        holder.subjects.text = "Mathematics"
        holder.rate.text = model.rate.toString()

        val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReferenceFromUrl("gs://tutor-finder-f8d8d.appspot.com/reindeer-5706627_640.png")

        GlideApp.with(holder.profilePic.context)
            .load(gsReference)
            .into(holder.profilePic)
    }

}