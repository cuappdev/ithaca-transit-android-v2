package com.cornellappdev.android.ithaca_transit.models

import com.squareup.moshi.Json

/*
Represents the type of route that is displayed on the map
 */
enum class DirectionType {
    @Json(name = "depart")
    BUS,
    @Json(name = "walk")
    WALK
}