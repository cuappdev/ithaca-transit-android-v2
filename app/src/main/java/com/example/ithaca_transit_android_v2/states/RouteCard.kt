package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.Models.Trip
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import java.util.Date

// Parent Class
sealed class RouteCard (
    val trip: Trip = Trip(
        emptyList(),
        Location("", Coordinate(0.0,0.0),""),
        Location("", Coordinate(0.0,0.0),""),
        false,
        Date(0),
        Date(0),
        0
    )
){}

/*
 * Route cards at the bottom of launch screen
 * Bus number displayed is obtained from [trip.routes]
 * Keeping all the parameters here to be safe,
 * but in practice some might not be needed
 */
class LaunchCardState(trip: Trip): RouteCard(trip = trip)

// A route card at launch screen is clicked
class LaunchCardClickedState(): RouteCard()

/*
 * A default display of a route card in route option view
 * [trip.isWalkingOnly] and number of [trip.routes] affects the drawing of transit details
 */
class CardFoldedDisplayState(trip: Trip): RouteCard(trip = trip)

// A route card at route option screen is clicked
class OptionCardClickedState(): RouteCard()

/*
 * [CardDetailDisplayState] is the state for the route detail view
 * Used when a card at either launch screen or route options is clicked
 */
class CardDetailDisplayState(trip: Trip): RouteCard(trip = trip)

// User clicks on the [Leave Now] button in route detail view and leave at dialog pops up
class LeaveAtClickedState: RouteCard()