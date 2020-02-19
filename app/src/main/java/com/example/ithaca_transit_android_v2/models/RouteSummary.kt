package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.JsonClass

//Class to hold route summary information.
@JsonClass(generateAdapter = true)
data class RouteSummary(
    val direction: directionSummary?,
    val stayOnBusForTransfer: Boolean?,
    val stopName: String?
)

// Data class to wrap the direction in route summary
@JsonClass(generateAdapter = true)
data class directionSummary(
    val busNumber: Int?,
    val type: DirectionType?
)