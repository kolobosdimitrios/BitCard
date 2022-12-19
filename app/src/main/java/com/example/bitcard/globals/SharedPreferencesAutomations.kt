package com.example.bitcard.globals

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SharedPreferencesHelpers {

    companion object{

        const val USER_CREDENTIALS_NAME = "user_credentials"

        fun clear(context: Context, name: String){
            context.getSharedPreferences(name, MODE_PRIVATE).edit().clear().apply()
        }

        fun create(context: Context, name: String){
            context.getSharedPreferences(name, MODE_PRIVATE)
        }

        fun write(context: Context, name: String, key: String, value: String){

            context.getSharedPreferences(name, MODE_PRIVATE).edit().putString(key, value).apply()

        }

        fun write(context: Context, name: String, key: String, value: Int){

            context.getSharedPreferences(name, MODE_PRIVATE).edit().putInt(key, value).apply()

        }

        fun write(context: Context, name: String, key: String, value: Float){

            context.getSharedPreferences(name, MODE_PRIVATE).edit().putFloat(key, value).apply()

        }

        fun write(context: Context, name: String, key: String, value: Boolean){

            context.getSharedPreferences(name, MODE_PRIVATE).edit().putBoolean(key, value).apply()

        }

        fun write(context: Context, name: String, key: String, value: Long){

            context.getSharedPreferences(name, MODE_PRIVATE).edit().putLong(key, value).apply()

        }

        fun readString(context: Context, name: String, key: String) : String? {
            return context.getSharedPreferences(name, MODE_PRIVATE).getString(key, null)
        }

        fun readInt(context: Context, name: String, key: String) : Int {
            return context.getSharedPreferences(name, MODE_PRIVATE).getInt(key, Int.MIN_VALUE)
        }

        fun readFloat(context: Context, name: String, key: String) : Float {
            return context.getSharedPreferences(name, MODE_PRIVATE).getFloat(key, Float.MIN_VALUE)
        }

        fun readBoolean(context: Context, name: String, key: String) : Boolean {
            return context.getSharedPreferences(name, MODE_PRIVATE).getBoolean(key, false)
        }

        fun readLong(context: Context, name: String, key: String) : Long {
            return context.getSharedPreferences(name, MODE_PRIVATE).getLong(key, Long.MIN_VALUE)
        }


    }
}