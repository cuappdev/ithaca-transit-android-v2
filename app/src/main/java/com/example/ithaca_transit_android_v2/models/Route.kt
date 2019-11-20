package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

// [Route] is a collection of [Direction] objects and other essential information
// to represent a way of getting to the destination
@JsonClass(generateAdapter = true)
data class Route (
    val directions: List<Direction>,
    val startCoords: Coordinate,
    val endCoords: Coordinate,
//    val isWalkingOnly: Boolean,
    @Json(name ="arrivalTime")
    val arrival: Date,
    @Json(name ="departureTime")
    val depart: Date
//    val boardInMin: Int
)