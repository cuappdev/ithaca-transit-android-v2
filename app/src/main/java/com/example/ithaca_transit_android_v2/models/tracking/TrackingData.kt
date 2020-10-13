package com.example.ithaca_transit_android_v2.models.tracking

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Direction
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class TrackingData (
    @Json(name ="bearing")
    val bearing: Int,
    @Json(name ="congestionLevel")
    val congestionLevel: Int,
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double,
    @Json(name = "routeId")
    val routeId: String,
    @Json(name = "speed")
    val speed: Double,
    @Json(name = "timestamp")
    val timestamp: Int,
    @Json(name = "tripId")
    val tripId: String
)