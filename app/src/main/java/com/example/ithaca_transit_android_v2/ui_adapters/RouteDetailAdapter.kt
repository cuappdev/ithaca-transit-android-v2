package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.Direction
import com.example.ithaca_transit_android_v2.models.DirectionType
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.models.Stop
import com.example.ithaca_transit_android_v2.views.BusNumberComponent
import com.example.ithaca_transit_android_v2.views.DirectionDot
import com.example.ithaca_transit_android_v2.views.DirectionLine
import kotlinx.android.synthetic.main.route_detailed_holder.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class RouteDetailAdapter(var context: Context, _routeDetail: View) {

    val routeDetail: View = _routeDetail
    val detailedLayout: LinearLayout = routeDetail.detailed_dynamic_layout


    val TOP_MARGIN = 30
    val TIME_LEFT_MARGIN = 50
    val TIME_RIGHT_MARGIN = 50
    val DOTS_LEFT_MARGIN = 25
    val BUS_ICON_LEFT_MARGIN = 50
    val WALKING_ICON_LEFT_MARGIN = 92
    val DESCRIPTION_LEFT_MARGIN = 80
    val DISTANCE_TOP_MARGIN = 5
    val SMALLDOT_LEFT_MARGIN = TIME_LEFT_MARGIN + TIME_RIGHT_MARGIN + 170
    val SMALLDOT_TOP_MARGIN = 20
    var detailedContext = context

    fun updateRouteDetail(route: Route) {

        val headerText = "Leaving in " + route.boardInMin.toString() // placeholder for now
        routeDetail.route_detail_header.text = headerText

        detailedLayout.removeAllViews()

        // TODO: Create views in route_detailed_holder_holder.xml
        // Dynamically set the views to the appropriate information in this method

        drawRouteCard(route)
    }

    //Creates views for dots and stop names
    private fun createDirectionLinearLayout(
        time: String,
        description: String,
        directionType: DirectionType,
        //Boolean values to fill in segments between two blue dots
        drawSegmentAbove: Boolean,
        drawSegmentBelow: Boolean,
        //Boolean to handle textview distance
        isDestination: Boolean
    ): LinearLayout {

        //Layout that holds dots and stop names
        val dotDirectionLayout = LinearLayout(detailedContext)
        dotDirectionLayout.orientation = LinearLayout.HORIZONTAL
        dotDirectionLayout.gravity = Gravity.CENTER_VERTICAL
        val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = DOTS_LEFT_MARGIN
        params.topMargin = TOP_MARGIN
        dotDirectionLayout.layoutParams = params

        val timeView = TextView(detailedContext)
        timeView.text = time
        timeView.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
        val timeParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        timeParams.leftMargin = TIME_LEFT_MARGIN
        timeParams.rightMargin = TIME_RIGHT_MARGIN
        timeView.layoutParams = timeParams

        dotDirectionLayout.addView(timeView)

        // Setting Dot Radius
        var radius = 16f
        if (isDestination) {
            radius = 20f
            params.leftMargin = params.leftMargin - 4
        }

        val colorStr: String = if (directionType == DirectionType.BUS) {
            "blue"
        } else {
            "gray"
        }
        val verticalPadding = 10f

        //Initialize Dots (Done indiviudally)
        val dot = DirectionDot(
            detailedContext, colorStr, isDestination, drawSegmentAbove,
            drawSegmentBelow, radius, 8f, verticalPadding
        )
        val size: Int = (radius * 2).toInt()

        //Create layout params for dots
        val canvasParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(size, size + 2 * verticalPadding.toInt())

        dot.layoutParams = canvasParams


        dotDirectionLayout.addView(dot)

        //Stop Name
        val descriptionView = TextView(detailedContext)
        descriptionView.text = description
        descriptionView.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
        val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        descriptionParams.leftMargin = DESCRIPTION_LEFT_MARGIN
        descriptionView.layoutParams = descriptionParams

        dotDirectionLayout.addView(descriptionView)

        return dotDirectionLayout
    }

    private fun createWalkingComponent(
        distance: String = "",
        hasDistance: Boolean = false
    ): LinearLayout {
        val walkingHolder = LinearLayout(detailedContext)
        walkingHolder.orientation = LinearLayout.HORIZONTAL




        val dotHolderparams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dotHolderparams.leftMargin = SMALLDOT_LEFT_MARGIN
        dotHolderparams.topMargin = SMALLDOT_TOP_MARGIN
        val dotsHolder = LinearLayout(detailedContext)
        dotsHolder.orientation = LinearLayout.VERTICAL

        dotsHolder.layoutParams = dotHolderparams

        val grayDotParams = ViewGroup.MarginLayoutParams(32, 27)
        grayDotParams.topMargin = 6

        val grayDot1 = DirectionDot(detailedContext, "gray", false, false, false, 6f, 0f, 0f)
        grayDot1.layoutParams = grayDotParams
        dotsHolder.addView(grayDot1)

        val grayDot2 = DirectionDot(
            detailedContext, "gray", false,
            false, false, 6f, 0f, 0f
        )
        grayDot2.layoutParams = grayDotParams
        dotsHolder.addView(grayDot2)


        //Add 3rd dto

        val grayDot3 = DirectionDot(
            detailedContext, "gray", false,
            false, false, 6f, 0f, 0f
        )
        grayDot3.layoutParams = grayDotParams
        dotsHolder.addView(grayDot3)


        walkingHolder.addView(dotsHolder)

        if (hasDistance) {
            //Distance Text
            val distanceView = TextView(detailedContext)
            distanceView.text = distance
            distanceView.setTextSize(10f)
            distanceView.setTextColor(ContextCompat.getColor(detailedContext, R.color.gray))
            val distanceParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            distanceParams.leftMargin = DESCRIPTION_LEFT_MARGIN
            distanceParams.topMargin = DISTANCE_TOP_MARGIN

            distanceView.layoutParams = distanceParams
            walkingHolder.addView(distanceView)
        }

        return walkingHolder
    }


    private fun drawRouteCard(route: Route) {
        val routeObj = route

        val directions: List<Direction> = routeObj.directions

        val firstD = directions[0]
        val firstDot =
            createDirectionLinearLayout("3:52 PM", firstD.name, firstD.type, false, false, false)

        detailedLayout.addView(firstDot)

        val smallerDots = createWalkingComponent("200", true)

        detailedLayout.addView(smallerDots)

    }

}