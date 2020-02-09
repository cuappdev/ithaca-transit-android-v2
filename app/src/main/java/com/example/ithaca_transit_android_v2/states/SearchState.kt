package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.LocationType

// Parent Class
sealed class SearchState

// Default search state
class SearchLaunchState : SearchState()

// Search state when user has no favorite or recent destinations
class EmptyInitClickState : SearchState()

/*
 * Subsequent launches of app, then clicking on search bar -> Displays Favorite and Recent
 * Destinations. **Will be different from EmptyInitSearchState after v.0**
 */
class InitClickState : SearchState()

// Immediately following InitClickState or EmptyInitClickState when user begins typing
class InitSearchState(
    val searchText:String
) : SearchState()

// Immediately following InitSearchState after network requests are made
class InitLocationsSearchState(
    val searchText: String,
    // Network call to all locations that match the search text
    val searchedLocations: List<Location>?
) : SearchState()

// Default start location is Current Location
class RouteDisplayState(
    val startLocation: Location,
    val endLocation: Location
) : SearchState()

/*
 * In the Direction Options state, when the user clicks either the start or end location,
 * automatically creating a blank typing field for changes
 */
class ChangeRouteState(
    val searchText: String,
    val hideSearchList: Boolean
) : SearchState()

/*
 * In the Direction Options state, when the user clicks either the start or end location,
 * and filled in with a searchedLocations network response
 */
class ChangeRouteLocationState(
    val searchText: String,
    val searchedLocations: List<Location>?,
    val hideSearchList: Boolean
) : SearchState()

