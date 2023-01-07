package com.example.bitcard.globals

import java.sql.Date
import java.text.SimpleDateFormat


class Time {

    companion object Formatter{

        /**
         * @timestamp: The date time in a string to be formatted.
         * @return: The date time string in a DateTime object.
         */
        fun format(timestamp: String) : String{
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.parse(timestamp)


            return dateFormat.format(date)
        }


    }

}