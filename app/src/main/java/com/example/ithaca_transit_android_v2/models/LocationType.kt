package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.Json

enum class LocationType {
    @Json(name = "busStop")
    BUS_STOP,
    @Json(name = "googlePlace")
    GOOGLE_PLACE
}