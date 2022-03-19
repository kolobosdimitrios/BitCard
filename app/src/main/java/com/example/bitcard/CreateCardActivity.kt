package com.example.bitcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.bitcard.databinding.ActivityCreateCardBinding
import com.example.bitcard.databinding.ActivityLoginBinding
import com.example.bitcard.network.Request
import com.example.bitcard.network.daos.CreateCardData

class CreateCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)

        binding.buttonCreateCard.setOnClickListener {
            if(binding.checkboxTermsAndConditions.isChecked) CreateCardData(
                    name = getValue(binding.name),
                    surname = getValue(binding.surname),
                    address = getValue(binding.streetAddress)
                )
        }
    }

    private fun getValue(editText: EditText) : String{
        return editText.text.trim().toString()
    }

}