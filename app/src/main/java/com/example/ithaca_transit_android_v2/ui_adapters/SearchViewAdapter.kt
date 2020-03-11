package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.Coordinate
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

        if (place.name === "Current Location") {
            viewHolder.placeIcon?.setBackgroundResource(R.drawable.ic_location_blue)
            viewHolder.placeLocTextView?.visibility = View.GONE
            viewHolder.placeNameTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        } else if (place.type == LocationType.BUS_STOP) {
            viewHolder.placeIcon?.setBackgroundResource(R.drawable.ic_bus_stop)
            viewHolder.placeLocTextView?.visibility = View.VISIBLE
            viewHolder.placeLocTextView?.text = "Bus Stop"
            viewHolder.placeNameTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        } else {
            viewHolder.placeIcon?.setBackgroundResource(R.drawable.ic_location)
            viewHolder.placeLocTextView?.visibility = View.VISIBLE
            viewHolder.placeLocTextView?.text = place.detail
            viewHolder.placeNameTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
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

    fun swapItems(locations: List<Location>, offerCurrentLocation: Boolean) {
        var mutableList = locations
        if (offerCurrentLocation) {
            mutableList = mutableList.toMutableList()
            val myLoc = Repository.currentLocation
            if (myLoc != null) {
                mutableList.add(0, Location(
                        LocationType.APPLE_PLACE, "Current Location",
                        Coordinate(myLoc.latitude, myLoc.longitude), ""
                    )
                )
            }
        }
        this.locations = mutableList
        notifyDataSetChanged()
    }
}