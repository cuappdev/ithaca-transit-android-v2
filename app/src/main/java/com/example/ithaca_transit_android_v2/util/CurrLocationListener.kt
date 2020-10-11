package com.example.ithaca_transit_android_v2.util

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import com.example.ithaca_transit_android_v2.Repository

// Used to keep track of the current location of the user at all times
class CurrLocationListener: LocationListener {

    override fun onLocationChanged(p0: Location) {
            Repository.currentLocation = p0
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }
}