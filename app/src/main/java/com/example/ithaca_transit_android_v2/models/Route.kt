package com.example.ithaca_transit_android_v2.models

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*
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
    @Json(name = "routeSummary")
    val routeSummary: List<RouteSummary>?,
    val boardInMin: Int

) {
    companion object {
        fun computeBoardInMin(firstBusDirection: Direction): Int {

            fun convertDate(date: Date) : Date {
                val sdf = SimpleDateFormat("h:mm")
                sdf.timeZone = TimeZone.getTimeZone("GMT-8:00")
                val newDate = sdf.format(date)

                return sdf.parse(newDate)

            }
            val diff = firstBusDirection.startTime.time - System.currentTimeMillis()
            return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS).toInt()
            //return millisToMins(diff)
        }
    }
}