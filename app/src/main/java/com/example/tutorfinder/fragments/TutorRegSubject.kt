package com.example.tutorfinder.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.textview.MaterialTextView
import java.util.*

class TutorRegSubject : Fragment(), View.OnClickListener {

    // Views
    private lateinit var addSubjectButton: TextView
    private lateinit var subjectFlexBox: FlexboxLayout
    private lateinit var currencyText: MaterialTextView
    private lateinit var perSpinner: Spinner

    // To store subjects view
    private val subjectsViewList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tutor_reg_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instantiate views
        addSubjectButton = requireView().findViewById(R.id.add_subject_text)
        subjectFlexBox = requireView().findViewById(R.id.subjects_flex_box)
        currencyText = requireView().findViewById(R.id.currency_text)
        perSpinner = requireView().findViewById(R.id.per_spinner)

        // Set onClick listener
        addSubjectButton.setOnClickListener(this)

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

    override fun onStart() {
        super.onStart()

        // To ensure that if device is locked at this screen and unlocked again
        // flexbox wouldn't have duplicate views
        subjectFlexBox.removeAllViews()

        // Inflate all the subject views
        for(subjectName in subjectsViewList) {
            val child = layoutInflater.inflate(R.layout.reg_subject_tag, null)
            val textBox = child.findViewById<MaterialTextView>(R.id.text_box)
            textBox.text = subjectName
            subjectFlexBox.addView(child)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_subject_text -> addSubject()
        }
    }

    private fun addSubject() {

        // Dialog prompt creation to ask subject name
        val builder = AlertDialog.Builder(requireContext())

        // Set dialog prompt title
        builder.setTitle(getString(R.string.enter_subject))

        // Inflate layout for content of dialog box
        val inflatedView = LayoutInflater.from(requireContext().applicationContext)
            .inflate(R.layout.add_subject_prompt, null)

        // Get input field
        val inputField = inflatedView.findViewById<EditText>(R.id.subject_field)

        // Create dialog
        val dialog = builder.setView(inflatedView)
            // Set up buttons inside prompt
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                addSubjectToScreen(inputField.text.toString())
            }
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.cancel()
            }
            .create()

        dialog.show()

    }

    private fun addSubjectToScreen(subjectName: String) {
        val child = layoutInflater.inflate(R.layout.reg_subject_tag, null)
        val textBox = child.findViewById<MaterialTextView>(R.id.text_box)
        textBox.text = subjectName
        subjectFlexBox.addView(child)

        // Add subject view to list
        subjectsViewList.add(subjectName)
    }

    // Return the currency of current locale
    private fun getCurrencySymbol(): CharSequence? {
        val defaultLocale = Locale.getDefault()
        val currency = Currency.getInstance(defaultLocale)
        return currency.symbol
    }

}