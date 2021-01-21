package com.example.tutorfinder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.google.android.material.textview.MaterialTextView
import java.util.*

class TutorRegSubject: Fragment() {

    // Views
    private lateinit var currencyText: MaterialTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tutor_reg_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        currencyText = requireView().findViewById(R.id.currency_text)

        // Set currency textview to appropriate symbol
        currencyText.text = getCurrencySymbol()
    }

    // Return the currency of current locale
    private fun getCurrencySymbol(): CharSequence? {
        val defaultLocale = Locale.getDefault()
        val currency = Currency.getInstance(defaultLocale)
        return currency.symbol
    }

}