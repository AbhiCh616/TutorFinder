package com.example.tutorfinder.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.example.tutorfinder.interfaces.BasicInfoListener
import com.example.tutorfinder.interfaces.SetAllEntries
import com.example.tutorfinder.utils.Gender
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class TutorRegBasicInfo : Fragment(), View.OnClickListener, SetAllEntries {

    companion object {
        private val TAG = TutorRegBasicInfo::class.qualifiedName
    }

    // Views
    private lateinit var name: TextInputEditText
    private lateinit var age: TextInputEditText
    private lateinit var genderMaleButton: MaterialButton
    private lateinit var genderFemaleButton: MaterialButton
    private lateinit var genderOtherButton: MaterialButton

    // To store user input
    private var gender: Gender? = null

    // To send data to Activity
    private var listener: BasicInfoListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tutor_reg_basic_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        name = requireActivity().findViewById(R.id.name)
        age = requireActivity().findViewById(R.id.age)
        genderMaleButton = requireActivity().findViewById(R.id.gender_male_button)
        genderFemaleButton = requireActivity().findViewById(R.id.gender_female_button)
        genderOtherButton = requireActivity().findViewById(R.id.gender_other_button)

        // Set onClick listeners
        genderMaleButton.setOnClickListener(this)
        genderFemaleButton.setOnClickListener(this)
        genderOtherButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        when(gender) {
            Gender.Male -> selectGenderMale()
            Gender.Female -> selectGenderFemale()
            Gender.Other -> selectGenderOther()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Set listener
        if(context is BasicInfoListener) {
            listener = context
        }
        else {
            Log.e(TAG, TAG + " must be " + BasicInfoListener::class.qualifiedName)
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    // To be called from Activity
    override fun setAllEntries() {
        listener?.setName(name.text.toString())
        listener?.setAge(age.text.toString().toInt())
        listener?.setGender(gender)
    }

    // To validate all the fields
    override fun validateForm(): Boolean {
        val nameValue = name.text.toString()
        val ageValue = age.text.toString()
        val genderValue = gender
        // Name field is empty
        if(nameValue.trim() == "") {
            Snackbar.make(requireView().findViewById(R.id.next_button),
                    getString(R.string.name_empty_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
                    .show()
            return false
        }
        // Age field is empty
        if(ageValue.trim() == "") {
            Snackbar.make(requireView().findViewById(R.id.next_button),
                    getString(R.string.age_empty_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
                    .show()
            return false
        }
        // Gender is not selected
        if(genderValue == null) {
            Snackbar.make(requireView().findViewById(R.id.next_button),
                    getString(R.string.gender_empty_error),
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
                    .show()
            return false
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.gender_male_button -> selectGenderMale()
            R.id.gender_female_button -> selectGenderFemale()
            R.id.gender_other_button -> selectGenderOther()
        }
    }

    private fun selectGenderMale() {
        // Set male button as selected and other gender buttons as unselected
        genderMaleButton.setBackgroundColor(getColor(requireContext(), R.color.dark_blue))
        genderFemaleButton.setBackgroundColor(getColor(requireContext(), R.color.grey_500))
        genderOtherButton.setBackgroundColor(getColor(requireContext(), R.color.grey_500))

        gender = Gender.Male
    }

    private fun selectGenderFemale() {
        // Set female button as selected and other gender buttons as unselected
        genderMaleButton.setBackgroundColor(getColor(requireContext(), R.color.grey_500))
        genderFemaleButton.setBackgroundColor(getColor(requireContext(), R.color.dark_blue))
        genderOtherButton.setBackgroundColor(getColor(requireContext(), R.color.grey_500))

        gender = Gender.Female
    }

    private fun selectGenderOther() {
        // Set "other" button as selected and other gender buttons as unselected
        genderMaleButton.setBackgroundColor(getColor(requireContext(), R.color.grey_500))
        genderFemaleButton.setBackgroundColor(getColor(requireContext(), R.color.grey_500))
        genderOtherButton.setBackgroundColor(getColor(requireContext(), R.color.dark_blue))

        gender = Gender.Other
    }

}