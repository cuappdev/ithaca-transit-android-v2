package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.util.CompositeOnItemClickListener

object Repository {
    init {
    }
    var currentLocation: android.location.Location? = null
    var destinationListListeners: CompositeOnItemClickListener? = null
    var changeRouteListeners: CompositeOnItemClickListener? = null
    var startLocation: Location? = null
    var destinationLocation: Location? = null
}