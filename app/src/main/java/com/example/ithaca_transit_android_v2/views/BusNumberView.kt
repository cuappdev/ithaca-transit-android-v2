package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.ithaca_transit_android_v2.R


class BusNumberView(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs){

    lateinit var busImg: ImageView
    lateinit var busNumb: TextView

    init {
        inflate(context, R.layout.busimage, this)

        busImg = findViewById<ImageView>(R.id.bus_image)
        busNumb = findViewById<TextView>(R.id.bus_number)




    }

    fun setBusNumber(number: Int){
        busNumb.setText(number.toString())
    }


}
