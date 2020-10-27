package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.ithaca_transit_android_v2.R


class WalkingManComponent(context: Context, layout: Int): FrameLayout(context){

    private var walkingImg: ImageView

    init {
        inflate(context, layout, this)
        walkingImg = findViewById(R.id.walking_image)
    }

}
