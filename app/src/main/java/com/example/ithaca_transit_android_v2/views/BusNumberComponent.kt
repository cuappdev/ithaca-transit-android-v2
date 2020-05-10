package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.ithaca_transit_android_v2.R


class BusNumberComponent(context: Context): FrameLayout(context){

    private var busImg: ImageView
    private var busNumber: TextView

    init {
        inflate(context, R.layout.bus_image, this)

        busImg = findViewById(R.id.bus_image)
        busNumber = findViewById(R.id.bus_number)


    }

    fun setBusNumber(number: Int) {
        busNumber.text = number.toString()
    }

}
