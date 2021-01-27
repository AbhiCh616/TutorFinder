package com.example.tutorfinder.adapters

import android.content.Context
import android.provider.Settings.Global.getString
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
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.storage.FirebaseStorage
import kotlin.coroutines.coroutineContext

class TutorBriefInfoAdapter(options: FirestoreRecyclerOptions<TutorInfo>,
                            private val onItemClicked: (String) -> Unit) :
        FirestoreRecyclerAdapter<TutorInfo, TutorBriefInfoAdapter.TutorBriefInfoViewHolder>(options) {

    inner class TutorBriefInfoViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
        val name: TextView = view.findViewById(R.id.name)
        val rating: TextView = view.findViewById(R.id.rating)
        val subjects: FlexboxLayout = view.findViewById(R.id.subjects_flex_box)
        val rate: TextView = view.findViewById(R.id.rate)
        val profilePic: ImageView = view.findViewById(R.id.profile_pic)
        val costFactor: TextView = view.findViewById(R.id.cost_factor)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
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
        holder.rate.text = (8377.toChar() + model.cost.toString())

        // Set cost factor
        holder.costFactor.text =
        when(model.costFactor) {
            "Month" -> holder.itemView.context.getString(R.string.s_per_month)
            "Hour" -> holder.itemView.context.getString(R.string.s_per_hour)
            else -> holder.itemView.context.getString(R.string.s_per_day)
        }

        // Insert subject tags
        for(subjectName in model.subjects) {
            addTag(subjectName, holder, holder.subjects)
        }


        // Get reference of profile pic stored in firebase storage
        val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReferenceFromUrl(model.profilePic)

        // Display profile pic from firebase cloud
        GlideApp.with(holder.profilePic.context)
                .load(gsReference)
                .circleCrop()
                .into(holder.profilePic)
    }

    fun addTag(subjectName: String, holder: RecyclerView.ViewHolder, subjectsField: FlexboxLayout) {
        val layoutInflater = holder.itemView.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val child = layoutInflater.inflate(R.layout.brief_subject_tag, null)
        child.tag = "subject_tag"
        val textBox = child.findViewById<MaterialTextView>(R.id.text_box)
        textBox.text = subjectName
        subjectsField.addView(child)
    }

}