package com.example.bitcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import com.example.bitcard.databinding.ActivityRegisterBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnFocusChangeListener {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.date.setOnFocusChangeListener(this)
    }

    private fun selectDate(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date_dialog_header))
                .build()

        datePicker.addOnPositiveButtonClickListener {
            //TODO get timestamp

            binding.date.setText(datePicker.headerText)
        }
        datePicker.show(supportFragmentManager, null)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1){ //has focus
            selectDate()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, StartupActivity::class.java))
        finish()
    }
}