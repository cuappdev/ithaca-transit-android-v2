package com.cornellappdev.android.ithaca_transit.states

import com.cornellappdev.android.ithaca_transit.models.Location
import com.cornellappdev.android.ithaca_transit.models.Route
import com.cornellappdev.android.ithaca_transit.models.RouteOptions

// Parent Class
sealed class RouteCardState()


class OptionsHiddenState() : RouteCardState()
/*
 * A display of the route cards after a start and end destination were set, displaying all possible
 * methods of getting from location A to location B
 */
class RouteListState(
    val startLocation: Location,
    val destLocation: Location,
    val routeOptions: RouteOptions?
) : RouteCardState()

/*
 * A route card from [RouteOptionState] is clicked.
 * [RouteDetailViewState] displays the selected route in detail.
 * Takes in only one [route] object because only one route is displayed.
 */
class RouteDetailViewState(
    val route: Route
) : RouteCardState()

/*
 * User clicks on the [Leave Now] button in [RouteOptionState] and a dialog pops up (UI change).
 * When the dialog box is set, do a networking call and go back to [RouteOptionState] with
 * the directions refreshed.
 */
class LeaveAtClickedState : RouteCardState()