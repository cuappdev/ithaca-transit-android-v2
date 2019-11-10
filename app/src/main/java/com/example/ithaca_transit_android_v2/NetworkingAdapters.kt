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
        var type: LocationType

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