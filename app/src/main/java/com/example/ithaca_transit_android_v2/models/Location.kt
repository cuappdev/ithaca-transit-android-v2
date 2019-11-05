package com.example.ithaca_transit_android_v2.models

//Location. - Represents either a bus stop or a google place.
data class Location (
    val name: String,
    val coordinate: Coordinate,
    val detail: String?
) {}    