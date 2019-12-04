package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date
import java.util.Calendar
import java.util.concurrent.TimeUnit

// [Route] is a collection of [Direction] objects and other essential information
// to represent a way of getting to the destination
@JsonClass(generateAdapter = true)
data class Route (
    val directions: List<Direction>,
    val startCoords: Coordinate,
    val endCoords: Coordinate,
    @Json(name ="arrivalTime")
    val arrival: Date,
    @Json(name ="departureTime")
    val depart: Date,
    val boardInMin: Int
) {
    companion object {
        fun computeBoardInMin(firstBusDirection: Direction): Int {
            val diff = firstBusDirection.startTime.time - Calendar.getInstance().time.time
            return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS).toInt()
        }
    }
}