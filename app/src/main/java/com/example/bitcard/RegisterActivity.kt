package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.bitcard.databinding.ActivityRegisterBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnFocusChangeListener {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BitCard)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.date.setOnFocusChangeListener(this)
    }

    private fun selectDate(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select dates")
                .build()

        datePicker.addOnPositiveButtonClickListener {
            //TODO get timestamp
        }
        datePicker.show(supportFragmentManager, null)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1){ //has focus
            selectDate()
        }
    }
}