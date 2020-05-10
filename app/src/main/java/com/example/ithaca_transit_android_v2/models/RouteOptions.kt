package com.example.ithaca_transit_android_v2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
 * [RouteOptions] is a collection of three groups of route objects
 * (fromStop, walking and boardingSoon) for the RouteOptions view
 */
@JsonClass(generateAdapter = true)
data class RouteOptions (
    val boardingSoon: List<Route>?,
    val fromStop: List<Route>?,
    val walking: List<Route>?
)