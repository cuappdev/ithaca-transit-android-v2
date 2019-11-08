package com.example.ithaca_transit_android_v2.models

import com.example.ithaca_transit_android_v2.Models.LocationType
import com.squareup.moshi.JsonClass

//Location. - Represents either a bus stop or a google place.
@JsonClass(generateAdapter = true)
data class Location (
    val type : LocationType,
    val name: String,
    val coordinate : Coordinate,
    val detail: String?
)