package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.Location

object Repository {
    init {
    }
    var currentLocation: android.location.Location? = null
    var startLocation: Location? = null
    var destinationLocation: Location? = null
}