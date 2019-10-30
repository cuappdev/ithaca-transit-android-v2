package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location

// Parent Class
sealed class SearchState(
    val startLocation: Location = Location("", Coordinate(0.0, 0.0), ""),
    val endLocation: Location = Location("", Coordinate(0.0, 0.0), "")
) {}

// Default UI when launching app - only a search bar (no interactions so far)
class LaunchState() : SearchState()

// First time launching app then clicking on the search bar, no favorite or recent destinations
class EmptyInitSearchState() : SearchState()

/*
 *Subsequent launches of app the clicking on search bar -> Displays Favorite and Recent
 *Destinations. **Will be different from EmptyInitSearchState after v.0**
 */
class InitClickState() : SearchState()

/*
 *Immediately following InitClickState when user begins typing.
 *Requires network call
 */
class InitSearchState(
    val searchText: String,
    // Network call to all locations that match the search text
    val searchedLocations: List<Location>
) : SearchState() {}

/*
 *[Start Destination name on Left] -> [Destination name on Right]
 *Default start location is Current Location
 */
class RouteDisplayState(
    startLocation: Location,
    endLocation: Location
) : SearchState(startLocation = startLocation, endLocation = endLocation) {}

/*
 *A clickable bar with Start Location name
 *A clickable bar with Destination name
 *No networks have been made yet
 */
class RouteOptionState(
    startLocation: Location,
    endLocation: Location
) : SearchState(startLocation = startLocation, endLocation = endLocation) {}

/*
 *In the Route Options state, when the user clicks either the start or end location
 *It automatically creates a blank typing field for changes
 *Network requirement
 */
class ChangeLocationState(
    // User Input
    searchText: String,
    searchedLocations: List<Location>
) : SearchState() {}


