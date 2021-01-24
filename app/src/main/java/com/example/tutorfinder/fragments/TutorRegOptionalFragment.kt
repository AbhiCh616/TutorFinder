package com.example.tutorfinder.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.example.tutorfinder.interfaces.BasicInfoListener
import com.example.tutorfinder.interfaces.SetAllEntries
import com.example.tutorfinder.interfaces.TutorRegOptionalListener
import com.google.android.material.textfield.TextInputEditText

class TutorRegOptionalFragment: Fragment(), SetAllEntries {

    companion object {
        private val TAG = TutorRegOptionalFragment::class.qualifiedName
    }

    // Views
    private lateinit var aboutText: TextInputEditText
    private lateinit var educationText: TextInputEditText
    private lateinit var experienceText: TextInputEditText

    // To send data to Activity
    private var listener: TutorRegOptionalListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tutor_reg_opt_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        aboutText = requireView().findViewById(R.id.about_field)
        educationText = requireView().findViewById(R.id.education_field)
        experienceText = requireView().findViewById(R.id.experience_field)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Set listener
        if(context is TutorRegOptionalListener) {
            listener = context
        }
        else {
            Log.e(TutorRegOptionalFragment.TAG, TutorRegOptionalFragment.TAG + " must be " +
                    TutorRegOptionalListener::class.qualifiedName)
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun setAllEntries() {
        listener?.setAbout(aboutText.text.toString())
        listener?.setEducation(educationText.text.toString())
        listener?.setExperience(experienceText.text.toString())
    }

    override fun validateForm(): Boolean {
        return true
    }


}