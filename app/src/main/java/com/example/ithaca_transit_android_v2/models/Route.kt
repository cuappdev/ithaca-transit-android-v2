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
        fun millisToMins(millis : Long) : Int{
            var millisS = millis/1000
            val sToMins = (millisS/60).toInt()
            return sToMins
        }
        fun computeBoardInMin(firstBusDirection: Direction): Int {
            Log.d("startTime", ""+firstBusDirection.startTime)
            Log.d("startTime", ""+firstBusDirection.startTime.time )

            fun convertDate(date: Date) : Date {
                val sdf = SimpleDateFormat("h:mm")
                sdf.timeZone = TimeZone.getTimeZone("GMT-8:00")
                val newDate = sdf.format(date)
                Log.d("FirstTimeNDate", ""+newDate)

                return sdf.parse(newDate)

            }

            Log.d("FirstTimeDate", ""+convertDate(firstBusDirection.startTime) )
            Log.d("FirstTime", ""+convertDate(firstBusDirection.startTime).time )
            Log.d("FirstTimeCurrent", ""+System.currentTimeMillis() )
            val diff = firstBusDirection.startTime.time - System.currentTimeMillis()

            Log.d("FirstTimeDiff", ""+diff)

            return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS).toInt()
            //return millisToMins(diff)
        }
    }
}