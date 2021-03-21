package com.cornellappdev.android.ithaca_transit.models

import com.squareup.moshi.Json

//Coordinate.kt - Represents the longitude and latitude of a Location
data class Coordinate(
    @Json(name = "lat")
    val latitude: Double,
    @Json(name = "long")
    val longitude: Double
){
    override fun toString(): String = "$latitude, $longitude"
}

