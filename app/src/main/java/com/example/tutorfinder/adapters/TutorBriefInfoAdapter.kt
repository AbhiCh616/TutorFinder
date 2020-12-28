package com.example.tutorfinder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.utils.GlideApp
import com.example.tutorfinder.R
import com.example.tutorfinder.models.TutorInfo
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage

class TutorBriefInfoAdapter(options: FirestoreRecyclerOptions<TutorInfo>,
                            private val onItemClicked: (String) -> Unit) :
    FirestoreRecyclerAdapter<TutorInfo, TutorBriefInfoAdapter.TutorBriefInfoViewHolder>(options) {

    inner class TutorBriefInfoViewHolder(view : View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val name: TextView = view.findViewById(R.id.name)
        val rating: TextView = view.findViewById(R.id.rating)
        val subjects: TextView = view.findViewById(R.id.subjects)
        val rate: TextView = view.findViewById(R.id.rate)
        val profilePic: ImageView = view.findViewById(R.id.profile_pic)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if(adapterPosition != RecyclerView.NO_POSITION) {
                onItemClicked(snapshots.getSnapshot(adapterPosition).id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorBriefInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tutor_info_brief_card, parent, false)
        return TutorBriefInfoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TutorBriefInfoViewHolder,
        position: Int,
        model: TutorInfo
    ) {
        holder.name.text = model.name
        holder.rating.text = model.rating.toString()
        holder.subjects.text = model.subjects[0]
        holder.rate.text = model.rate.toString()

        val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReferenceFromUrl(model.profilePic)

        GlideApp.with(holder.profilePic.context)
            .load(gsReference)
            .into(holder.profilePic)
    }

}