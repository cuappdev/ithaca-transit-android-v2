package com.example.ithaca_transit_android_v2.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ListView
import com.example.ithaca_transit_android_v2.R

/* This class is a ListView that has a maximum height of 250 pixels. It is used by the main SearchView to
* display a list of available options. We can't just wrap the ListView in some layout of constant height
* because then the ListView would always have a constant height, we want it to expand up to a specific
* height. */
class MaxHeightListView(context: Context, attributeSet: AttributeSet) :
    ListView(context, attributeSet) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxHeightMeasureSpec = heightMeasureSpec
        val value = if (this.id == R.id.locations_list) {
            250f
        } else {
            200f
        }
        val maxHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value, getResources().getDisplayMetrics()
        )
        maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight.toInt(), MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
    }
}