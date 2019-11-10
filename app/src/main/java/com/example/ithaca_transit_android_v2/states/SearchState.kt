package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.LocationType
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location

// Parent Class
sealed class SearchState(
    val startLocation: Location = Location(LocationType.BUS_STOP,"", Coordinate(0.0, 0.0), ""),
    val endLocation: Location = Location(LocationType.BUS_STOP,"", Coordinate(0.0, 0.0), "")
)

// Default search state
class SearchLaunchState : SearchState()

// Search state when user has no favorite or recent destinations
class EmptyInitClickState : SearchState()

/*
 * Subsequent launches of app, then clicking on search bar -> Displays Favorite and Recent
 * Destinations. **Will be different from EmptyInitSearchState after v.0**
 */
class InitClickState : SearchState()

// Immediately following InitClickState or EmptyInitClickState when user begins typing.
class InitSearchState(
    val searchText: String,
    // Network call to all locations that match the search text
    val searchedLocations: List<Location>
) : SearchState()

// Default start location is Current Location
class RouteDisplayState(
    startLocation: Location,
    endLocation: Location
) : SearchState(startLocation, endLocation)

/*
 * A clickable bar with Start Location name
 * A clickable bar with Destination name
 */
class SearchRouteOptionState(
    startLocation: Location,
    endLocation: Location
) : SearchState(startLocation, endLocation)

/*
 * In the Route Options state, when the user clicks either the start or end location,
 * automatically creating a blank typing field for changes
 */
class ChangeLocationState(
    searchText: String, // User Input
    searchedLocations: List<Location>
) : SearchState()