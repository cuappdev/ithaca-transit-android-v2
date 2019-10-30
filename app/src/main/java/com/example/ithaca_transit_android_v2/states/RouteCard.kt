package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.Models.Route
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import java.util.*

// Parent Class
sealed class RouteCard (
    val routes: List<Route> = emptyList(),
    val startLocation: Location = Location("", Coordinate(0.0,0.0),""),
    val endLocation: Location = Location("", Coordinate(0.0,0.0),""),
    val isWalkingOnly: Boolean = false,
    val arrival: Date = Date(0),
    val depart: Date = Date(0),
    val boardInMin: Int = 0
){}

/* Route cards at the bottom of launch screen
   Bus number displayed is obtained from [routes]
   Keeping all the parameters here to be safe,
   but in practice some might not be needed
*/
class LaunchCardState(
    routes: List<Route>,
    startLocation: Location,
    endLocation: Location,
    isWalkingOnly: Boolean,
    arrival: Date,
    depart: Date,
    boardInMin: Int
): RouteCard(
    routes = routes,
    startLocation = startLocation,
    endLocation = endLocation,
    isWalkingOnly = isWalkingOnly,
    arrival = arrival,
    depart = depart,
    boardInMin = boardInMin
)

// A route card at launch screen is clicked
class LaunchCardClickedState(): RouteCard()

// A default display of a route card in route option view
// [isWalkingOnly] and number of [routes] affects the drawing of transit details
class CardFoldedDisplayState(
    routes: List<Route>,
    startLocation: Location,
    endLocation: Location,
    isWalkingOnly: Boolean,
    arrival: Date,
    depart: Date,
    boardInMin: Int
): RouteCard(
    routes = routes,
    startLocation = startLocation,
    endLocation = endLocation,
    isWalkingOnly = isWalkingOnly,
    arrival = arrival,
    depart = depart,
    boardInMin = boardInMin
)

// A route card at route option screen is clicked
class OptionCardClickedState(): RouteCard()

// [CardDetailDisplayState] is the state for the route detail view
// Used when a card at either launch screen or route options is clicked
class CardDetailDisplayState(
    routes: List<Route>,
    startLocation: Location,
    endLocation: Location,
    isWalkingOnly: Boolean,
    arrival: Date,
    depart: Date,
    boardInMin: Int
): RouteCard(
    routes = routes,
    startLocation = startLocation,
    endLocation = endLocation,
    isWalkingOnly = isWalkingOnly,
    arrival = arrival,
    depart = depart,
    boardInMin = boardInMin
)

// User clicks on the [Leave Now] button in route detail view and leave at dialog pops up
class LeaveAtClickedState: RouteCard()