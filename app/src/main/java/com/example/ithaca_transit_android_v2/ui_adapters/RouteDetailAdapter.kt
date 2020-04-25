package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.view.View
import com.example.ithaca_transit_android_v2.models.Route
import kotlinx.android.synthetic.main.route_detailed_holder.view.*

class RouteDetailAdapter(var context: Context, _routeDetail:View) {
    val routeDetail:View = _routeDetail

    fun updateRouteDetail(route: Route) {

        val headerText = "Leaving in "+route.boardInMin.toString() // placeholder for now
        routeDetail.route_detail_header.text = headerText

        // TODO: Create views in route_detailed_holder_holder.xml
        // Dynamically set the views to the appropriate information in this method
    }
}