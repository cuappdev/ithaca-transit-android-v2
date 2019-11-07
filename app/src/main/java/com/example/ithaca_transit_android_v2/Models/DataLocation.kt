package com.example.ithaca_transit_android_v2.Models

import com.example.ithaca_transit_android_v2.LocationAdapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
// This class is used to store all of the Location objects that come from backend
@JsonClass(generateAdapter = true)
class DataLocation(
    @Json(name = "data")
    val data: List<LocationAdapter.JsonLocation>
)