package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.JsonClass
import java.util.Date

// [Route] is a collection of [Direction] objects and other essential information
// to represent a way of getting to the destination
@JsonClass(generateAdapter = true)
data class Route (
    val directions: List<Direction>,
    val startLocation: Coordinate,
    val endLocation: Coordinate,
    val isWalkingOnly: Boolean,
    val arrival: Date,
    val depart: Date,
    val boardInMin: Int
)