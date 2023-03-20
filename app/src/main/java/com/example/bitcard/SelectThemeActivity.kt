package com.example.bitcard

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bitcard.databinding.ActivitySelectThemeBinding

class SelectThemeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectThemeBinding
    private lateinit var sharedPrefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivitySelectThemeBinding.inflate(layoutInflater)

        val toolbar =  binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        sharedPrefsEditor = sharedPrefs.edit()

        when (sharedPrefs.getString("theme", "system")) {
            "dark" -> {
                binding.darkRadioButton.isChecked = true
            }

            "light" -> {
                binding.lightRadioButton.isChecked = true
            }

            "system" -> {
                binding.followSystemRadioButton.isChecked = true
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onRadioButtonClicked(view: View) {
        if(view is RadioButton){
            val checked= view.isChecked //is the button checked
            when (view.id){
                R.id.darkRadioButton ->
                    if ( checked ){
                        changeThemeToDark()
                    }
                R.id.lightRadioButton ->
                    if( checked ){
                        changeThemeToLight()
                    }
                R.id.follow_systemRadioButton ->
                    if( checked ){
                        followSystemTheme()
                    }
            }
        }
    }

    private fun changeThemeToDark(){
        saveUserChoiceToSharedPrefs("dark")
        setDefaultNightMode(MODE_NIGHT_YES)
    }

    private fun changeThemeToLight() {
        saveUserChoiceToSharedPrefs("light")
        setDefaultNightMode(MODE_NIGHT_NO)

    }

    private fun followSystemTheme() {
        saveUserChoiceToSharedPrefs("system")
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)

    }

    private fun saveUserChoiceToSharedPrefs(choice: String) {
        sharedPrefsEditor.putString("theme", choice).commit()

    }

}