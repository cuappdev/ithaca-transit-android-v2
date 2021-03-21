package com.cornellappdev.android.ithaca_transit.util

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.cornellappdev.android.ithaca_transit.Repository

// Used to keep track of the current location of the user at all times
class CurrLocationListener: LocationListener {

    override fun onLocationChanged(p0: Location) {
            Repository.currentLocation = p0
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }
}