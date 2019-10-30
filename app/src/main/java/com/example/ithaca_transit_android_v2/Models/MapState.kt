package com.example.ithaca_transit_android_v2.Models

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
sealed class MapState(
    val trip:Trip = Trip(
        emptyList(),
        Location("", Coordinate(0.0, 0.0), ""),
        Location("", Coordinate(0.0, 0.0), ""),
        false,
        null,
        null,
        0)
) {}

class LaunchState():MapState()

class LaunchStateFilled(
    busStops: List<Coordinate>
): MapState()

class DestinationSearch():MapState() {}

class TripOptions(
    firstBusTrip: Trip,
    walkingTrip: Trip
): MapState(){}

class SelectedTrip(
    selectedTrip: Trip
):MapState(
    selectedTrip
){}