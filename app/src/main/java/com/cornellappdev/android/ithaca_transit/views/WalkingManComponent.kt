package com.cornellappdev.android.ithaca_transit.views

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import com.cornellappdev.android.ithaca_transit.R


class WalkingManComponent(context: Context, layout: Int): FrameLayout(context){

    private var walkingImg: ImageView

    init {
        inflate(context, layout, this)
        walkingImg = findViewById(R.id.walking_image)
    }

}
