package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.LocationType

class SearchViewAdapter(context: Context, private var locations: List<Location> ):
    BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class ViewHolder(element: View?) {
        var placeNameTextView: TextView? = null
        var placeLocTextView: TextView? = null
        var placeIcon: ImageView? = null

        init {
            this.placeNameTextView = element?.findViewById<TextView>(R.id.name)
            this.placeLocTextView = element?.findViewById<TextView>(R.id.location)
            this.placeIcon = element?.findViewById<ImageView>(R.id.location_icon)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_searchview, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val place = locations[position]
        viewHolder.placeNameTextView?.text = place.name

        if (place.type == LocationType.BUS_STOP) {
            viewHolder.placeIcon?.setBackgroundResource(R.drawable.ic_bus_stop)
            viewHolder.placeLocTextView?.text = "Bus Stop"
        } else {
            viewHolder.placeIcon?.setBackgroundResource(R.drawable.ic_location)
            viewHolder.placeLocTextView?.text = place.detail
        }
        return view
    }

    override fun getItem(i: Int): Location {
        return locations[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return locations.size
    }

    fun swapItems(locations: List<Location>) {
        this.locations = locations
        notifyDataSetChanged()
    }
}