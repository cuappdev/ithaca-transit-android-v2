package com.example.ithaca_transit_android_v2

import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.LocationType
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
class LocationAdapter {
    class DataLocation(
        @Json(name = "data") val data: List<JsonLocation>
    )

    class JsonLocation(
        @Json(name = "type") val type: String,
        @Json(name = "name") val name: String,
        @Json(name = "lat") val lat: Double,
        @Json(name = "long") val long: Double,
        @Json(name = "detail") val detail: String?
    )

    @FromJson
    private fun fromJson(json: DataLocation): List<Location> {
        var type: LocationType

        val finalLoc = json.data.map { loc ->

            when (loc.type) {
                "BUS_STOP" -> type = LocationType.GOOGLE_PLACE
                else -> type = LocationType.BUS_STOP
            }

            Location(type, loc.name, Coordinate(loc.lat, loc.long), loc.detail)
        }
        return finalLoc
    }

    @ToJson
    private fun toJson(json: List<Location>): DataLocation {

        val final = json.map { loc ->
            JsonLocation(
                loc.type.toString(),
                loc.name,
                loc.coordinate.latitude,
                loc.coordinate.longitude,
                loc.detail
            )
        }
        return DataLocation(final)
    }
}