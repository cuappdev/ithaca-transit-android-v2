package com.example.ithaca_transit_android_v2.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Direction(
    val type : DirectionType,
    @Json(name = "path")
    val listOfCoordinates: List<Coordinate>,
    val startTime: Date,
    val endTime: Date,
    val name : String,
    @Json(name = "startLocation")
    val startCoords: Coordinate,
    @Json(name = "endLocation")
    val endCoords: Coordinate,
    @Json(name = "stops")
    val busStops: List<Stop>,
    @Json(name = "routeId")
    val routeId: String?,
    @Json(name = "tripIds")
    val tripIds: List<String>,
    //val delay: Int
    val busNumber: Int?,
    val distance: Double
)