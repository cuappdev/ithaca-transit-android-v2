package com.example.ithaca_transit_android_v2.models

import java.util.Date

// [Route] is a collection of [Direction] objects and other essential information
// to represent a way of getting to the destination
data class Route (
    val directions: List<Direction>,
    val startLocation: Location,
    val endLocation: Location,
    val isWalkingOnly: Boolean,
    val arrival: Date,
    val depart: Date,
    val boardInMin: Int
) {}