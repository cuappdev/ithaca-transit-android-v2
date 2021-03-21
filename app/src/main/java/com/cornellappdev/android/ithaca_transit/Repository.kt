package com.cornellappdev.android.ithaca_transit

import com.cornellappdev.android.ithaca_transit.models.Coordinate
import com.cornellappdev.android.ithaca_transit.models.Location
import com.cornellappdev.android.ithaca_transit.models.LocationType
import com.cornellappdev.android.ithaca_transit.models.Route

object Repository {
    init {
    }
    // this sets the location to the middle of Cornell's campus
    var defaultLocation = Location(
        type = LocationType.APPLE_PLACE,
        name = "Cornell",
        coordinate = Coordinate(42.4491, -76.4833),
        detail = "default location for map zoom")

    var currentLocation: android.location.Location? = null
    var startLocation: Location? = null
    var destinationLocation: Location? = null
    var isPermissionGranted = false

    // Called from the SearchView everytime the RouteOptionsView should change
    var _updateRouteFromSearch = fun(hidden: Boolean) {}

    // Called from RouteAdapter each time a Route is clicked
    var _updateRouteDetailed = fun(route: Route) {}

    // Called from the SearchView everytime the MapView should change
    var _updateMapView = fun(a:Route) {}

    // Called when the map should not display any routes at all
    var _clearMapView = fun() {}
}