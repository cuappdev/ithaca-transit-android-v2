package com.example.ithaca_transit_android_v2.Models

import com.example.ithaca_transit_android_v2.models.Location
import java.util.*

// [Trip] is a collection of [Route] objects and other essential information
// to represent a way of getting to the destination
data class Trip (
    val routes: List<Route>,
    val startLocation: Location,
    val endLocation: Location,
    val isWalkingOnly: Boolean,
    val arrival: Date,
    val depart: Date,
    val boardInMin: Int
) {}