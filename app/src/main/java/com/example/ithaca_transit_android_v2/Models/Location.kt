package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.JsonClass

//Location. - Represents either a bus stop or a google place.
@JsonClass(generateAdapter = true)
data class Location (
    val name: String,
    val lat: Double,
    val long: Double,
    val detail: String?
) {}    