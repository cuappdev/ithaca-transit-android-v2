package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.models.RouteOptions

// Parent Class
sealed class RouteCardState(val routes: RouteOptions? = null)

/*
 * A default display of a route card in route option view.
 * [route.isWalkingOnly] and number of [route.directions] affects the drawing of transit details.
 */
class RouteOptionState(searchedRoute : RouteOptions): RouteCardState(routes = searchedRoute)

/*
 * A route card from [RouteOptionState] is clicked.
 * [RouteDetailViewState] displays the selected route in detail.
 * Takes in only one [route] object because only one route is displayed.
 */
class RouteDetailViewState(route: Route): RouteCardState()

/*
 * User clicks on the [Leave Now] button in [RouteOptionState] and a dialog pops up (UI change).
 * When the dialog box is set, do a networking call and go back to [RouteOptionState] with
 * the directions refreshed.
 */
class LeaveAtClickedState : RouteCardState()