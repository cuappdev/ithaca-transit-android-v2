package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.Location
import java.util.*

//Default
class SearchState

//Default UI when launching app. Just displays a search bar (no interactions so far)
data class LaunchState(

){}

//First time using the app, after clicking on the search bar, there will be nothing
//but "Search For a Destination" text and a big magnifying glass
data class EmptyInitSearchState(

){}

//Click on search bar -> Changes UI by dropping a list of Favorite Destinations and
//recent destinations
data class InitSearchState(
    val favDestinations : List<Location>,
    val recentDestinations : List<Location>
){}



//Immediately following InitSearchState when user begins typing. Requires network call
data class InitNetworkState(
    val  searchText : String,
    //Network call to all locations that match the search text
    val  searchedLocations : List<Location>
){}

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
data class RouteDisplayState(
    val startLocation: Location,
    val endLocation: Location,
    val date : Date
){}

//How to get to Route Options state
//1)From Route Display State, click on either the start or end location names
//2)Click on a bus stop on map. Automatically fills the start location with the Bus Stop
//Brings them to the Route Options Menu -
// 2. A clickable bar with Start Location name - User can click it, but won't make any network calls
// 3. A clickable bar with Destination name - User can click it, but won't make any network calls
// 4. Underneath the Search will be populated by Favorite Destinations and Recents
data class RouteOptionState(
    val startLocation: Location,
    val endLocation: Location,
    val favDestinations : List<Location>,
    val recentDestinations : List<Location>


){}

//In the Route Options state, when the user clicks either the start or end location
//It automatically creates a black typing field for the User to enter their new
//desired start or end location name
//NO Network requirment yet
data class ChangeLocationState(
    //What the user inputs
    val searchText : String,
    //Network call to all locations that match the search text
    val  searchedLocations : List<Location>
){}


//When the User clicks on the clock icon to specify a time they want to leave
//Creates a pop up that allows user to choose a time for arrival or destination
data class TimeState(
    val leaveTime : Time,
    val arriveTime: Time,
    val latest : Boolean
){}


