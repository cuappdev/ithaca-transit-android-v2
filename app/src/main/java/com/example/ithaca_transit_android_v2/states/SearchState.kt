package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location

//Parent Search State Class - Contains Default parameters
sealed class SearchState(
    val startLocation: Location = Location("", Coordinate(0.0,0.0),""),
    val endLocation: Location = Location("", Coordinate(0.0,0.0),"") ,
    val favDestinations: List<Location> = mutableListOf(),
    val recentDestinations: List<Location> = mutableListOf()
){}

//Default UI when launching app. Just displays a search bar (no interactions so far)
class LaunchState(
): SearchState(){}

//First time using the app, after clicking on the search bar, there will be nothing
//but "Search For a Destination" text and a big magnifying glass
class EmptyInitSearchState(
): SearchState(){}

//Click on search bar -> Changes UI by dropping a list of Favorite Destinations and
//recent destinations
class InitSearchState(
    favDestinations : List<Location>,
    recentDestinations : List<Location>
): SearchState(favDestinations = favDestinations, recentDestinations = recentDestinations)

//Immediately following InitSearchState when user begins typing. Requires network call
class InitNetworkState(
    val  searchText : String,
    //Network call to all locations that match the search text
    val  searchedLocations : List<Location>
): SearchState(){}

// 2 Possible Ways to get to Route Display State
// 1)
// After typing in desired location from initial search, user clicks on one of
// the drop down locations.
// 2)
// After successfully setting a start and end location from the route options menu
//
// Brings them to the Route Display State -
// Start Destination name on Left -> Destination name on Right
// Default start location is Current Location
class RouteDisplayState(
    startLocation: Location,
    endLocation: Location
): SearchState(startLocation = startLocation, endLocation = endLocation){}

//How to get to Route Options state
//1)From Route Display State, click on either the start or end location names
//2)Click on a bus stop on map. Automatically fills the start location with the Bus Stop
//Brings them to the Route Options Menu -
// 2. A clickable bar with Start Location name - User can click it, but won't make any network calls
// 3. A clickable bar with Destination name - User can click it, but won't make any network calls
// 4. Underneath the Search will be populated by Favorite Destinations and Recent Destinations
class RouteOptionState(
    startLocation: Location,
    endLocation: Location,
    favDestinations : List<Location>,
    recentDestinations : List<Location>

): SearchState(startLocation,endLocation,favDestinations,recentDestinations){}

//In the Route Options state, when the user clicks either the start or end location
//It automatically creates a black typing field for the User to enter their new
//desired start or end location name
//NO Network requirment yet
class ChangeLocationState(
    //What the user inputs
    searchText : String,
    //Network call to all locations that match the search text
    searchedLocations : List<Location>
): SearchState(){}


