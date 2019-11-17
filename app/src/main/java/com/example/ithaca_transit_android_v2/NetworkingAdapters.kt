package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.*
import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*
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

@JsonClass(generateAdapter = true)
class RouteAdapter {
    class DataRoute(
        val data: DataRouteOptions
    )

    @JsonClass(generateAdapter = true)
    class DataRouteOptions(
        @Json(name = "boardingSoon")
        val boardingSoon: List<JsonRoute>,
        @Json(name = "fromStop")
        val fromStop: List<JsonRoute>,
        @Json(name = "walking")
        val walking: List<JsonRoute>

    )

    @JsonClass(generateAdapter = true)
    class JsonRoute(
        @Json(name = "directions")
        val directions: List<JsonDirection>,
        @Json(name = "startCoords")
        val startLocation: JsonLocation,
        @Json(name = "endCoords")
        val endLocation: JsonLocation,
        // TODO: need to calculate isWalkingOnly
        @Json(name = "arrivalTime")
        val arrival: String, // e.g. 2019-11-06T17:07:20Z
        @Json(name = "departureTime")
        val depart: String
        // TODO: need to calculate BoardInMin
    )

    @JsonClass(generateAdapter = true)
    class JsonDirection(
        @Json(name = "path")
        val listOfCoordinates: List<Coordinate>,
        @Json(name = "startTime")
        val startTime: String,
        @Json(name = "endTime")
        val endTime: String,
        @Json(name = "startLocation")
        val startLocation: JsonLocation,
        @Json(name = "endLocation")
        val endLocation: JsonLocation,
        @Json(name = "stops")
        val busStops: List<JsonLocation>,
        @Json(name = "routeNumber")
        val busNumber: Int
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
    private fun fromJson(json: DataRoute) : RouteOptions {
        fun processStringDate(dateString: String): Date {
            val dateAdapt = CustomDateAdapter()
            return dateAdapt.fromJson(dateString)
        }

        fun processStops(locs: List<JsonLocation>): List<Location> {
            return locs.map{ loc -> Location(LocationType.BUS_STOP, loc.name,
                Coordinate(loc.lat, loc.long), loc.detail)}
        }

        fun processDirections(jsonDirection: List<JsonDirection>): List<Direction> {
            return jsonDirection.map { dir -> Direction(
                dir.listOfCoordinates.map { c -> Coordinate(c.latitude, c.longitude) },
                processStringDate(dir.startTime),
                processStringDate(dir.endTime),
                Coordinate(dir.startLocation.lat, dir.startLocation.long),
                Coordinate(dir.endLocation.lat, dir.endLocation.long),
                processStops(dir.busStops),
                dir.busNumber
            )}
        }

        fun computeBoardInMin(firstBusDirection: Direction): Int {
            val diff =  firstBusDirection.startTime.time - Calendar.getInstance().time.time
            return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS).toInt()
        }

        fun processRoutesBus(jsonRoute: List<JsonRoute>): List<Route> {
            return jsonRoute.map { route -> Route(
                processDirections(route.directions),
                Coordinate(route.startLocation.lat, route.startLocation.long),
                Coordinate(route.endLocation.lat, route.endLocation.long),
                false,
                processStringDate(route.arrival),
                processStringDate(route.depart),
                computeBoardInMin(processDirections(route.directions)[1])
                // index [1] is accessed because the second direction is the first bus direction
            ) }
        }

        fun processRoutesWalk(jsonRoute: List<JsonRoute>): List<Route> {
            return jsonRoute.map { route -> Route(
                processDirections(route.directions),
                Coordinate(route.startLocation.lat, route.startLocation.long),
                Coordinate(route.endLocation.lat, route.endLocation.long),
                true,
                processStringDate(route.arrival),
                processStringDate(route.depart),
                0   // [boardInMin] for a walking route is set to be 0
            ) }
        }

        return RouteOptions(
            processRoutesBus(json.data.boardingSoon),
            processRoutesBus(json.data.fromStop),
            processRoutesWalk(json.data.walking)

        )
    }

    @ToJson
    private fun toJson (routeOpt: RouteOptions): DataRoute{
        // TODO: this function hasn't been implemented
        return DataRoute(DataRouteOptions(emptyList(), emptyList(), emptyList()))
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