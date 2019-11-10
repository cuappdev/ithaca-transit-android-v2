package com.example.ithaca_transit_android_v2.states

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.Route

sealed class MapState(routes: List<Route>)

//State that represents the empty map before a network call has been
class MapLaunchState(): MapState(emptyList())

//State that represents the map with all bus stops marked and marks the user's current location.
class LaunchStateFilled(
    busStops: List<Location>,
    currentLocation: Coordinate
) : MapState(emptyList())

//state that shows the first trip option and the walking trip.
class TripOptions(
    firstBusRoute: Route,
    walkingRoute: Route
): MapState(listOf(firstBusRoute, walkingRoute))

//state that shows just selected path
class SelectedTrip(
    selectedRoute: Route
): MapState(listOf(selectedRoute))
