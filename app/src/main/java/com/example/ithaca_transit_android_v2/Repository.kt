package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.Route

object Repository {
    init {
    }
    var currentLocation: android.location.Location? = null
    var startLocation: Location? = null
    var destinationLocation: Location? = null

    // Called from the SearchView everytime the RouteOptionsView should change
    var _updateRouteOptions = fun() {}

    // Called from the SearchView everytime the MapView should change
    var _updateMapView = fun(a:Route) {}
}