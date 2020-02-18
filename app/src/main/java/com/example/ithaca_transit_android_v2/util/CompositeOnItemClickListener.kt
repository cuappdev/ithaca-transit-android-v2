package com.example.ithaca_transit_android_v2.util

import android.util.Log
import android.view.View
import android.widget.AdapterView

// This class is used to add multiple listeners to the listview for onCliclks
class CompositeOnItemClickListener : AdapterView.OnItemClickListener {

    private var listeners: ArrayList<AdapterView.OnItemClickListener>? = null

    init {
        listeners = ArrayList()
    }
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        for (l in listeners!!) {
            l.onItemClick(p0, p1, p2, p3)
        }
    }

    fun addOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        listeners!!.add(listener)
    }
}