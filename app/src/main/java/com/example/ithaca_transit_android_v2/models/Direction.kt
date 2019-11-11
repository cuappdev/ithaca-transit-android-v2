package com.example.ithaca_transit_android_v2.models
import java.util.*

data class Direction(
    val listOfCoordinates: List<Coordinate>,
    val startTime: Date,
    val endTime: Date,
    val startLocation: Coordinate,
    val endLocation: Coordinate,
    val busStops: List<Location>,
    val busNumber: Int
)