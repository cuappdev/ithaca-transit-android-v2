package com.example.ithaca_transit_android_v2.models

/*
 * [RouteOptions] is a collection of three groups of route objects
 * (fromStop, walking and boardingSoon) for the RouteOptions view
 */
data class RouteOptions (
    val fromStop: List<Route>,
    val walking: List<Route>,
    val boardingSoon: List<Route>
){}