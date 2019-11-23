package com.example.ithaca_transit_android_v2.models
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
// TODO: deprecate this class 
data class Stop(
    val stopID: String,
    val lat: Double,
    val long: Double
)