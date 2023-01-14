package com.example.bitcard.globals

import java.text.SimpleDateFormat


class Time {

    companion object Formatter{

        /**
         * @timestamp: The date time in a string to be formatted.
         * @return: The date time string in a DateTime object. ex. = "Monday 22 December, 2022"
         */
        fun format(timestamp: String) : String{
            var dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.parse(timestamp)
            dateFormat = SimpleDateFormat("EEEE dd MMMM, yyyy")

            date?.let {
                return dateFormat.format(date)
            }

            throw IllegalStateException()
        }


    }

}