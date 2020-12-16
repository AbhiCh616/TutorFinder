package com.example.tutorfinder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SelectRoleActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_role)

        findViewById<Button>(R.id.student).setOnClickListener(this)
        findViewById<Button>(R.id.teacher).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.student -> triggerStudentFlow()
            R.id.teacher -> triggerTeacherFlow()
        }
    }

    private fun triggerTeacherFlow() {
        TODO("Not yet implemented")
    }

    private fun triggerStudentFlow() {
        val studentIntent = Intent(this, MainActivity::class.java)
        startActivity(studentIntent)
    }
}