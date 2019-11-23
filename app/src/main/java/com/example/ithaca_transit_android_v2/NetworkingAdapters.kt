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
        var firstBus = if (json.directions[0].type == DirectionType.BUS) 1 else 0
        var boardInMins: Int = if (json.directions.size != 1) Route.computeBoardInMin(json.directions[firstBus]) else 0
        return Route(json.directions, json.startCoords, json.endCoords, json.arrival, json.depart,
            boardInMins)
    }
}
class CustomDateAdapter{
    private val serverFormat = ("yyyy-MM-dd'T'HH:mm:ss'Z'")
    private val dateFormat = SimpleDateFormat(serverFormat, Locale.getDefault())
    @FromJson
    fun fromJson(date: String): Date {
        return dateFormat.parse(date)
    }
    @ToJson
    fun toJson(writer: JsonWriter, value: Date?) {
        value?.let { writer.value(value.toString()) }
    }
}