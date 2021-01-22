package com.example.tutorfinder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.google.android.material.button.MaterialButton

class TutorRegBasicInfo : Fragment(), View.OnClickListener {

    // Views
    private lateinit var genderMaleButton: MaterialButton
    private lateinit var genderFemaleButton: MaterialButton
    private lateinit var genderOtherButton: MaterialButton

    // To store user input
    private enum class Gender {Male, Female, Other}
    private var gender: Gender? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tutor_reg_basic_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
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