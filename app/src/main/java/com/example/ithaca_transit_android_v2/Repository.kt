package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.Location

object Repository {
    init {
    }
    private var currentLocation: android.location.Location? = null
    private var startLocation: Location? = null
    private var destinationLocation: Location? = null

    fun setCurrentLocation(loc: android.location.Location) {
        this.currentLocation = loc
    }

    fun setStartLocation(loc: Location) {
        this.startLocation = loc
    }

    fun setDestinationLocation(loc: Location) {
        this.destinationLocation = loc
    }

    fun getCurrentLocation(): android.location.Location? {
        return currentLocation
    }

    fun getStartLocation(): Location? {
        return startLocation
    }

    fun getDestinationLocation(): Location? {
        return destinationLocation
    }
}