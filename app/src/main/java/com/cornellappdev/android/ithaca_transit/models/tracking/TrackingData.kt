package com.cornellappdev.android.ithaca_transit.models.tracking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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