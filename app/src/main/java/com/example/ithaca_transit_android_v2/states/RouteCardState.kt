package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.Models.Trip

// Parent Class
sealed class RouteCardState (
    val trips: List<Trip> = listOf()
)

/*
 * Route cards at the bottom of launch screen.
 * Bus number displayed is obtained from [trip.routes].
 */
class LaunchCardState(trips: List<Trip>): RouteCardState(trips = trips)

/*
 * A default display of a route card in route option view.
 * [trip.isWalkingOnly] and number of [trip.routes] affects the drawing of transit details.
 */
class RouteOptionState(trips: List<Trip>): RouteCardState(trips = trips)

/*
 * A route card at [RouteOptionState] or [LaunchCardState] is clicked.
 * [RouteDetailViewState] displays the selected trip in detail.
 * Takes in only one [trip] object because only one trip is displayed.
 */
class RouteDetailViewState(trip: Trip): RouteCardState(listOf(trip))

/*
 * User clicks on the [Leave Now] button in [RouteOptionState] and a dialog pops up (UI change).
 * When the dialog box is set, do a networking call and go back to [RouteOptionState] with
 * the routes refreshed.
 */
class LeaveAtClickedState: RouteCardState()