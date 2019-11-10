package com.example.ithaca_transit_android_v2.Models
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import java.util.*

data class Route(
    val listOfCoordinates: List<Coordinate>,
    val startTime: Date,
    val endTime: Date,
    val startLocation: Location,
    val endLocation: Location,
    val busStops: List<Location>,
    val busNumber: Int
) {}