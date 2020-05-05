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
    var _updateRouteFromSearch = fun(hidden: Boolean) {}

    // Called from RouteAdapter each time a Route is clicked
    var _updateRouteDetailed = fun(route: Route) {}

    // Called from the SearchView everytime the MapView should change
    var _updateMapView = fun(a:Route) {}

    // Called when the map should not display any routes at all
    var _clearMapView = fun() {}
}