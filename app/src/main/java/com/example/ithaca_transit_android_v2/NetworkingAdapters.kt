package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
class LocationAdapter {
    class DataLocation(
        val data: List<JsonLocation>
    )

    @JsonClass(generateAdapter = true)
    class JsonLocation(
        val type: LocationType,
        val name: String,
        val lat: Double,
        val long: Double,
        val detail: String?
    )

    @FromJson
    private fun fromJson(json: DataLocation): List<Location> {
        val finalLoc = json.data.map { loc ->
            Location(loc.type, loc.name, Coordinate(loc.lat, loc.long), loc.detail)
        }
        return finalLoc
    }

    @ToJson
    private fun toJson(json: List<Location>): DataLocation {
        val final = json.map { loc ->
            JsonLocation(
                loc.type,
                loc.name,
                loc.coordinate.latitude,
                loc.coordinate.longitude,
                loc.detail
            )
        }
        return DataLocation(final)
    }
}



class RouteAdapter{
    class JsonRoute(
        val directions: List<Direction>,
        val startCoords: Coordinate,
        val endCoords: Coordinate,
        @Json(name ="arrivalTime")
        val arrival: Date,
        @Json(name ="departureTime")
        val depart: Date
    )

    @FromJson
    private fun fromJson(json: JsonRoute): Route {
        fun computeBoardInMin(firstBusDirection: Direction): Int {
            val diff = firstBusDirection.startTime.time - Calendar.getInstance().time.time
            return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS).toInt()
        }

        var firstBus = 0
        var boardInMins = 0
        if(json.directions[0].type != DirectionType.BUS){
            firstBus = 1
        }
        if(json.directions.size != 1){
            boardInMins = computeBoardInMin(json.directions[firstBus])
        }

        return Route(json.directions, json.startCoords, json.endCoords, json.arrival, json.depart,
            boardInMins)
    }

    @ToJson
    private fun toJson(route: Route): JsonRoute {
        return JsonRoute(emptyList(), Coordinate(0.0,0.0),
            Coordinate(0.0,0.0), Calendar.getInstance().time,
            Calendar.getInstance().time)
    }

}

class CustomDateAdapter{
    private val dateFormat = SimpleDateFormat(SERVER_FORMAT, Locale.getDefault())

    @FromJson
    fun fromJson(date: String): Date {
        return dateFormat.parse(date)
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            writer.value(value.toString())
        }
    }

    companion object {
        const val SERVER_FORMAT = ("yyyy-MM-dd'T'HH:mm:ss'Z'") // define your server format here
    }
}