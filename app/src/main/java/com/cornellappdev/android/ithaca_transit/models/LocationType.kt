package com.cornellappdev.android.ithaca_transit.models

import com.squareup.moshi.Json

enum class LocationType {
    @Json(name = "busStop")
    BUS_STOP,
    @Json(name = "applePlace")
    APPLE_PLACE

}