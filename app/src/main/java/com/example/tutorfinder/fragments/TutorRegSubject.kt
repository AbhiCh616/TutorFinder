package com.example.tutorfinder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.textview.MaterialTextView
import java.util.*

class TutorRegSubject : Fragment() {

    // Views
    private lateinit var currencyText: MaterialTextView
    private lateinit var perSpinner: Spinner
    private lateinit var subjectFlexBox: FlexboxLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tutor_reg_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        currencyText = requireView().findViewById(R.id.currency_text)
        perSpinner = requireView().findViewById(R.id.per_spinner)
        subjectFlexBox = requireView().findViewById(R.id.subjects_flex_box)

        // Set currency textview to appropriate symbol
        currencyText.text = getCurrencySymbol()

        // Set up spinner
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.per_array,
                R.layout.item_per
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.item_per_dropdown)
            // Apply the adapter to the spinner
            perSpinner.adapter = adapter
        }

    }

    // Return the currency of current locale
    private fun getCurrencySymbol(): CharSequence? {
        val defaultLocale = Locale.getDefault()
        val currency = Currency.getInstance(defaultLocale)
        return currency.symbol
    }

}