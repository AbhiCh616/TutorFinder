package com.example.tutorfinder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorfinder.adapters.TutorBriefInfoAdapter
import com.example.tutorfinder.models.TutorInfo
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentSearchFragment: Fragment() {

    // Firebase Firestore
    private val db: FirebaseFirestore = Firebase.firestore
    private val tutorsRef: CollectionReference = db.collection("tutors")

    // Recyclerview adapter
    private var adapter: TutorBriefInfoAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.student_search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Query to fetch tutors list from Firestore
        val query: Query = tutorsRef.orderBy("name", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<TutorInfo> = FirestoreRecyclerOptions.Builder<TutorInfo>()
            .setQuery(query, TutorInfo::class.java)
            .build()

        // Set up RecyclerView
        val tutorListRecyclerView: RecyclerView = requireView().findViewById(R.id.rv_tutor_list)
        tutorListRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TutorBriefInfoAdapter(options) { docRef: String ->
            val intent = Intent(activity, TutorDetailsActivity::class.java)
            intent.putExtra("docRef", docRef)
            startActivity(intent)
        }
        tutorListRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        // RecyclerView adapter
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // RecyclerView adapter
        adapter?.stopListening()
    }
}