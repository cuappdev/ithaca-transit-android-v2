package com.example.ithaca_transit_android_v2.models

//Location.kt
data class Location (
    val name: String,
    val coordinate: Coordinate,
    val detail: String?
) {}