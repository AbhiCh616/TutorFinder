package com.example.tutorfinder.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tutorfinder.R
import com.example.tutorfinder.interfaces.BasicInfoListener
import com.example.tutorfinder.interfaces.SetAllEntries
import com.example.tutorfinder.interfaces.SubjectListener
import com.example.tutorfinder.utils.PerCostFactor
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.util.*
import java.util.regex.Pattern

class TutorRegSubject : Fragment(), View.OnClickListener, SetAllEntries {

    companion object {
        private val TAG = TutorRegSubject::class.qualifiedName
    }

    // Views
    private lateinit var addSubjectButton: TextView
    private lateinit var subjectFlexBox: FlexboxLayout
    private lateinit var currencyText: MaterialTextView
    private lateinit var cost: TextInputEditText
    private lateinit var perSpinner: Spinner

    // To store subjects view
    private val subjectNameList: MutableList<String> = mutableListOf()

    // To send data to Activity
    private var listener: SubjectListener? = null

    // To store data
    private var perCostFactor: PerCostFactor = PerCostFactor.Month

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
        cost = requireView().findViewById(R.id.cost)
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

        perSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    1 -> perCostFactor = PerCostFactor.Month
                    2 -> perCostFactor = PerCostFactor.Day
                    3 -> perCostFactor = PerCostFactor.Hour
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    override fun onStart() {
        super.onStart()

        // To ensure that if device is locked at this screen and unlocked again
        // flexbox wouldn't have duplicate views
        subjectFlexBox.removeAllViews()

        // Inflate all the subject views
        for (subjectName in subjectNameList) {
            createTagOnScreen(subjectName)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Set listener
        if (context is SubjectListener) {
            listener = context
        } else {
            Log.e(TutorRegSubject.TAG, TutorRegSubject.TAG + " must be " + BasicInfoListener::class.qualifiedName)
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun setAllEntries() {
        listener?.setSubject(subjectNameList)
        listener?.setCost(cost.text.toString().toInt())
        listener?.setCostFactor(perCostFactor)
    }

    override fun validateForm(): Boolean {
        return true
    }

    override fun onClick(v: View) {
        when (v.tag) {
            "subject_tag" -> deleteTag(v)
        }

        when (v.id) {
            R.id.add_subject_text -> addSubject()
        }
    }

    private fun deleteTag(v: View) {
        // Remove text from subject list
        val textView = v.findViewById<TextView>(R.id.text_box)
        subjectNameList.remove(textView.text)

        // Remove the tag from screen
        subjectFlexBox.removeView(v)
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
                    addSubjectToScreen(inputField.text.toString().trim().toLowerCase(Locale.ROOT))
                }
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.cancel()
                }
                .create()

        dialog.show()

    }

    private fun addSubjectToScreen(subjectName: String) {

        // Subject is empty
        if (subjectName == "") {
            return
        }

        // String contains space
        if (subjectName.contains(' ')) {
            Snackbar.make(addSubjectButton, getString(R.string.subject_space_warning), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
                    .show()
            return
        }

        // String contains anything other than a-z
        if (!Pattern.matches("[a-z]+", subjectName)) {
            Snackbar.make(addSubjectButton, getString(R.string.subject_alpha_warning), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
                    .show()
            return
        }

        // Subject is already selected
        if (subjectNameList.contains(subjectName)) {
            Snackbar.make(addSubjectButton, getString(R.string.subject_already_warning), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
                    .show()
            return
        }

        // Add subject view to flexbox
        createTagOnScreen(subjectName)

        // Add subject view to list
        subjectNameList.add(subjectName)
    }

    private fun createTagOnScreen(subjectName: String) {
        val child = layoutInflater.inflate(R.layout.reg_subject_tag, null)
        child.tag = "subject_tag"
        child.setOnClickListener(this)
        val textBox = child.findViewById<MaterialTextView>(R.id.text_box)
        textBox.text = subjectName
        subjectFlexBox.addView(child)
    }


    // Return the currency of current locale
    private fun getCurrencySymbol(): CharSequence? {
        val defaultLocale = Locale.getDefault()
        val currency = Currency.getInstance(defaultLocale)
        return currency.symbol
    }

}