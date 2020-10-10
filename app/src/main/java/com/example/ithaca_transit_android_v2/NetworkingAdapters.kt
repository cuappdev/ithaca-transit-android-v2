package com.example.ithaca_transit_android_v2

import android.util.Log
import com.example.ithaca_transit_android_v2.models.*
import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@JsonClass(generateAdapter = true)
class LocationAdapter {

    class DataLocation(
        val data: PlacesLocation?
    )

    class PlacesLocation(
        val applePlaces: List<JsonLocation>?,
        val busStops: List<JsonLocation>?
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
        if (json.data == null) {
            return ArrayList()
        }
        val applePlaces = json.data.applePlaces?.let {
            json.data.applePlaces.map { loc ->
                Location(loc.type, loc.name, Coordinate(loc.lat, loc.long), loc.detail)
            }
        } ?: ArrayList()

        val busStops = json.data.busStops?.let {
            json.data.busStops.map { loc ->
                Location(loc.type, loc.name, Coordinate(loc.lat, loc.long), loc.detail)
            }
        } ?: ArrayList()
        val places = ArrayList<Location>()
        places.addAll(applePlaces)
        places.addAll(busStops)
        return places
    }

    @ToJson
    private fun toJson(json: List<Location>): PlacesLocation {
        val applePlaces = ArrayList<JsonLocation>()
        val busStops = ArrayList<JsonLocation>()
        for (place in json) {
            val jsonPlace = JsonLocation(
                place.type,
                place.name,
                place.coordinate.latitude,
                place.coordinate.longitude,
                place.detail
            )
            if (place.type == LocationType.APPLE_PLACE) {
                applePlaces.add(jsonPlace)
            } else {
                busStops.add(jsonPlace)
            }
        }
        return PlacesLocation(applePlaces, busStops)
    }
}

class RouteAdapter {
    class JsonRoute(
        val directions: List<Direction>,
        val startCoords: Coordinate,
        val endCoords: Coordinate,
        @Json(name = "arrivalTime")
        val arrival: Date,
        @Json(name = "departureTime")
        val depart: Date,
        val routeSummary: List<RouteSummary>,
        val travelDistance : Double,
        @Json(name = "endName")
        val endDestination: String

    )

    @FromJson
    private fun fromJson(json: JsonRoute): Route {
        val firstBus = if (json.directions[0].type == DirectionType.BUS) 0 else 1
        val boardInMins: Int =
            if (json.directions.size != 1) Route.computeBoardInMin(json.directions[firstBus]) else 0

        //Temporary code to deal with routes without a route summary
        if (!json.routeSummary.isNullOrEmpty()) {
            return Route(
                json.directions,
                json.startCoords,
                json.endCoords,
                json.arrival,
                json.depart,
                json.routeSummary,
                boardInMins,
                null,
                json.travelDistance,
                json.endDestination
            )

        } else {
            return Route(
                json.directions,
                json.startCoords,
                json.endCoords,
                json.arrival,
                json.depart,
                listOf(RouteSummary(directionSummary(-1, null), false, "noSummary")),
                boardInMins,
                null,
                json.travelDistance,
                json.endDestination
            )
        }

    }
}

class CustomDateAdapter {
    private val serverFormat = ("yyyy-MM-dd'T'HH:mm:ss")
    private val dateFormat = SimpleDateFormat(serverFormat)

    @FromJson
    fun fromJson(date: String): Date {

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val result = formatter.parse(date)

        return result
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Date?) {
        value?.let { writer.value(value.toString()) }
    }
}