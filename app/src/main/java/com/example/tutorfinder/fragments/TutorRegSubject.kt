package com.example.tutorfinder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.google.android.material.textview.MaterialTextView
import java.util.*

class TutorRegSubject : Fragment() {

    // Views
    private lateinit var currencyText: MaterialTextView
    private lateinit var perSpinner: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tutor_reg_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        currencyText = requireView().findViewById(R.id.currency_text)
        perSpinner = requireView().findViewById(R.id.per_spinner)

        // Set currency textview to appropriate symbol
        currencyText.text = getCurrencySymbol()

        // Set values for spinner
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.per_array,
                android.R.layout.simple_spinner_item)
                .also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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