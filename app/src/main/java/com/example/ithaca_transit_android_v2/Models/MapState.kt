package com.example.ithaca_transit_android_v2.Models

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import java.util.Date

sealed class MapState(
    trips: List<Trip>
)

class LaunchState():MapState(emptyList())

class LaunchStateFilled(
    busStops: List<Coordinate>
): MapState(emptyList())

class BusStopsShown():MapState(emptyList())

class TripOptions(
    firstBusTrip: Trip,
    walkingTrip: Trip
): MapState(listOf(firstBusTrip, walkingTrip))

class SelectedTrip(
    selectedTrip: Trip
):MapState(listOf(selectedTrip))