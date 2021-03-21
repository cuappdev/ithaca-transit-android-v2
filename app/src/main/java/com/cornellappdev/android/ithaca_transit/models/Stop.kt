package com.cornellappdev.android.ithaca_transit.models
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class Stop(
    val stopId: String,
    val lat: Double,
    val long: Double,
    val name: String
)