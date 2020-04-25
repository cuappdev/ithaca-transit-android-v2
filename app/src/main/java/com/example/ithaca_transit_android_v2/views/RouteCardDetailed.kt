package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.ithaca_transit_android_v2.R

class RouteCardDetailed (context: Context): LinearLayout(context) {

    init{
        LayoutInflater.from(context).inflate(R.layout.route_detailed, this, true)
        orientation = VERTICAL
    }

    private val detailed_linearlayout : LinearLayout = findViewById(R.id.route_detailed_inner)

    private fun createTopRoute (){}
    



}