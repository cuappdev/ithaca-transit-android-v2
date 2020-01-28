package com.example.ithaca_transit_android_v2.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ListView

/* This class is a ListView that has a maximum height of 250 pixels. It is used by the Search View to
* display a list of available options */
class MaxHeightListView(context: Context, attributeSet: AttributeSet): ListView(context, attributeSet) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxHeightMeasureSpec = heightMeasureSpec
        val maxHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            250f, getResources().getDisplayMetrics())
        maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight.toInt(), MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
    }
}