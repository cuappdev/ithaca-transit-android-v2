package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
import com.example.ithaca_transit_android_v2.views.BusExpandable
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

    val TOP_MARGIN = 15
    val TIME_LEFT_MARGIN = 50
    val TIME_RIGHT_MARGIN = 50
    val DOTS_LEFT_MARGIN = 25
    val BUS_ICON_LEFT_MARGIN = 50
    val WALKING_ICON_LEFT_MARGIN = 92
    val DESCRIPTION_LEFT_MARGIN = 60
    val DISTANCE_TOP_MARGIN = 5
    val BUS_LEFT_MARGIN = TIME_LEFT_MARGIN + TIME_RIGHT_MARGIN + 167
    val SMALLDOT_LEFT_MARGIN = TIME_LEFT_MARGIN + TIME_RIGHT_MARGIN + 165
    val SMALLDOT_TOP_MARGIN = 20
    var detailedContext = context

    fun updateRouteDetail(route: Route) {

        val headerText = "Leaving in " + route.boardInMin.toString() // placeholder for now
        routeDetail.route_detail_header.text = headerText

        detailedLayout.removeAllViews()

        val directions = route.directions

        //List of bus numbers
        val busNums : List<Int> =
            route.routeSummary.map { summaryObj -> summaryObj.direction?.busNumber ?: "" } as List<Int>

        val busIterator = busNums.iterator()

        val stopNames: List<String> =
            route.routeSummary.map { summaryObj -> summaryObj.stopName ?: "" }

        for( d in 0..directions.size-1){
            val direction = directions[d]
            if(direction.type == DirectionType.WALK){
                walkingDirection(direction, false)
            }
            else if (direction.type == DirectionType.BUS){
                busDirection(direction, busIterator.next())
            }

        }
        walkingDirection(directions[directions.size-1], true)

//
//        if (!stopNames.contains(Repository.startLocation?.name)) {
//            val walkingToLine = createDirectionLinearLayout(
//                "3:52 PM",
//                "Walk to",
//                route.directions[0].name,
//                route.directions[0].type,
//                drawSegmentAbove = false,
//                drawSegmentBelow = false,
//                isFinalDestination = false
//            )
//
//            detailedLayout.addView(walkingToLine)
//            val smallerDotsArea = createWalkingComponent("200");
//            detailedLayout.addView(smallerDotsArea)
//        }
//        val walkingToLine = createDirectionLinearLayout(
//            "3:52 PM",
//            "Board",
//            route.directions[1].name,
//            route.directions[1].type,
//            drawSegmentAbove = false,
//            drawSegmentBelow = true,
//            isFinalDestination = false,
//            busNumber = 72
//        )
//
//        detailedLayout.addView(walkingToLine)
//
//
//
//        //
//        val expandedTop = BusExpandable(detailedContext, route.directions[1])
//
//        val expandedTopParams = ViewGroup.MarginLayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        expandedTop.layoutParams = expandedTopParams
//
//        detailedLayout.addView(expandedTop)
//
//        val bottomExpanded = createDirectionLinearLayout(
//            "3:52 PM",
//            "Board",
//            route.directions[1].name,
//            route.directions[1].type,
//            drawSegmentAbove = true,
//            drawSegmentBelow = false,
//            isFinalDestination = false,
//            busNumber = 72,
//            expandedBottom = true
//        )
//
//        detailedLayout.addView(bottomExpanded)






        for (direction in route.directions) {

        }
    }

    private fun walkingDirection(direction: Direction, isFinal: Boolean) {

        val walkingToLine = createDirectionLinearLayout(
            "3:52 PM",
            "Walk to",
            direction.name,
            direction.type,
            drawSegmentAbove = false,
            drawSegmentBelow = false,
            isFinalDestination = isFinal
        )


        detailedLayout.addView(walkingToLine)
        val smallerDotsArea = createWalkingComponent("" + direction.distance.toInt());
        detailedLayout.addView(smallerDotsArea)

    }

    private fun busDirection(direction: Direction, busNum : Int) {

        val walkingToLine =
            createDirectionLinearLayout(
                "3:52 PM",
                "Board",
                direction.name,
                direction.type,
                drawSegmentAbove = false,
                drawSegmentBelow = true,
                isFinalDestination = false,
                busNumber = busNum.toInt()
            )


        detailedLayout.addView(walkingToLine)

        val expandedTop = BusExpandable(detailedContext, direction)

        val expandedTopParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        expandedTop.layoutParams = expandedTopParams

        detailedLayout.addView(expandedTop)

        val bottomExpanded = createDirectionLinearLayout(
            "3:52 PM",
            "Board",
            direction.name,
            direction.type,
            drawSegmentAbove = true,
            drawSegmentBelow = false,
            isFinalDestination = true,
            expandedBottom = true
        )

        detailedLayout.addView(bottomExpanded)



    }

    //Create Views for Times
    private fun createTimeText(time: String): TextView {

        val textView = TextView(detailedContext)
        textView.text = time
        textView.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
        val timeParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        timeParams.leftMargin = TIME_LEFT_MARGIN
        timeParams.rightMargin = TIME_RIGHT_MARGIN
        textView.layoutParams = timeParams

        return textView

    }

    //Creates views for times, dots and stop names
    private fun createDirectionLinearLayout(
        time: String,
        movementDescription: String, //"Walk to", "Get off at", etc.
        destination: String,
        directionType: DirectionType,
        //Boolean values to fill in segments between two blue dots
        drawSegmentAbove: Boolean,
        drawSegmentBelow: Boolean,
        //Boolean to handle textview distance
        isFinalDestination: Boolean,
        //Default Bus Value
        busNumber: Int = 0,
        expandedBottom: Boolean = false
    ): LinearLayout {

//        //Set DrawSegmentBelow. Bottom Blue dot expanded stops will have a abovesegment
//        var drawSegmentAbove = drawSegmentAbove
//
//        if(directionType == DirectionType.BUS && !expandedBottom){
//            drawSegmentAbove = true
//        }

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

        val timeText = createTimeText(time)

        dotDirectionLayout.addView(timeText)

        // Setting Dot Radius
        var radius = 16f
        if (isFinalDestination) {
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
            detailedContext, colorStr, isFinalDestination, drawSegmentAbove,
            drawSegmentBelow, radius, 8f, verticalPadding
        )
        val size: Int = (radius * 2).toInt()

        //Create layout params for dots
        val canvasParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(size, size + 2 * verticalPadding.toInt())

        dot.layoutParams = canvasParams
        dotDirectionLayout.addView(dot)

        //Stop Name
        if (directionType == DirectionType.WALK) {
            val descriptionView = TextView(detailedContext)

            val infoText = String.format("%s %s", movementDescription, destination)
            val sb = SpannableStringBuilder(infoText)
            val bss = StyleSpan(android.graphics.Typeface.BOLD)
            sb.setSpan(
                bss,
                movementDescription.length + 1,
                infoText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            descriptionView.text = infoText

            descriptionView.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
            val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            descriptionParams.leftMargin = DESCRIPTION_LEFT_MARGIN
            descriptionView.layoutParams = descriptionParams

            dotDirectionLayout.addView(descriptionView)
        }

        //Add an image of the bus
        else if (directionType == DirectionType.BUS && !expandedBottom) {

            val busTextLayout = LinearLayout(detailedContext)
            busTextLayout.orientation = LinearLayout.HORIZONTAL
            busTextLayout.gravity = Gravity.CENTER_VERTICAL
            val paramsBus: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            paramsBus.leftMargin = DESCRIPTION_LEFT_MARGIN
            //paramsBus.topMargin = TOP_MARGIN
            busTextLayout.layoutParams = paramsBus

            //First Text
            //TODO Add spannable elements
            val firstDescription = TextView(detailedContext)

            firstDescription.text = movementDescription

            firstDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
            val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            //descriptionParams.leftMargin = DESCRIPTION_LEFT_MARGIN
            firstDescription.layoutParams = descriptionParams

            busTextLayout.addView(firstDescription)

            //Bus image in text
            val busIconParams = ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            busIconParams.leftMargin = 7
            busIconParams.rightMargin = 7

            val busNumberView = BusNumberComponent(detailedContext)
            busNumberView.setBusNumber(busNumber)
            busNumberView.layoutParams = busIconParams

            val busIconHolder = LinearLayout(detailedContext)
            val busIconHolderParams = ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            busIconHolder.layoutParams = busIconHolderParams
            busIconHolder.addView(busNumberView)

            busTextLayout.addView(busIconHolder)

            //Second Part of text
            val secondDescription = TextView(detailedContext)

            secondDescription.text = destination

            secondDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
            val descriptionParams2: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            //descriptionParams.leftMargin = DESCRIPTION_LEFT_MARGIN
            secondDescription.layoutParams = descriptionParams2

            busTextLayout.addView(secondDescription)

            dotDirectionLayout.addView(busTextLayout)

        } else if (expandedBottom) {
            val descriptionView = TextView(detailedContext)

            val infoText = String.format("%s %s", "Get off at ", destination)
            val sb = SpannableStringBuilder(infoText)
            val bss = StyleSpan(android.graphics.Typeface.BOLD)
            sb.setSpan(
                bss,
                movementDescription.length + 1,
                infoText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            descriptionView.text = infoText

            descriptionView.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
            val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            descriptionParams.leftMargin = DESCRIPTION_LEFT_MARGIN
            descriptionView.layoutParams = descriptionParams

            dotDirectionLayout.addView(descriptionView)
        }


        return dotDirectionLayout
    }

    private fun makeSmallGrayDot(): View {
        val grayDotParams = ViewGroup.MarginLayoutParams(32, 27)
        grayDotParams.topMargin = 6
        val grayDot = DirectionDot(
            context = detailedContext,
            colorStr = "gray",
            useNestedCircles = false,
            drawSegmentBelow = false,
            drawSegmentAbove = false,
            radius = 6f,
            lineWidth = 0f,
            verticalPadding = 0f
        )
        grayDot.layoutParams = grayDotParams
        return grayDot
    }

//    private fun createBusExpandable(): LinearLayout {
//        val busHolder = LinearLayout(detailedContext)
//        busHolder.orientation = LinearLayout.HORIZONTAL
//
//        //Create blue line
//        val directionLineParams = ViewGroup.MarginLayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        directionLineParams.leftMargin = BUS_LEFT_MARGIN
//        val directionLine = DirectionLine(detailedContext, "blue", 100f, 8f)
//        directionLine.layoutParams = directionLineParams
//
//        busHolder.addView(directionLine)
//
//        //Create expandable portion
//
//        return busHolder
//
//    }

    private fun createWalkingComponent(distance: String): LinearLayout {
        val walkingHolder = LinearLayout(detailedContext)
        walkingHolder.orientation = LinearLayout.HORIZONTAL
        val dotHolderparams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dotHolderparams.leftMargin = SMALLDOT_LEFT_MARGIN
        dotHolderparams.topMargin = SMALLDOT_TOP_MARGIN
        val dotsHolder = LinearLayout(detailedContext)
        dotsHolder.orientation = LinearLayout.VERTICAL
        dotsHolder.layoutParams = dotHolderparams

        val grayDot1 = makeSmallGrayDot()
        val grayDot2 = makeSmallGrayDot()
        val grayDot3 = makeSmallGrayDot()
        dotsHolder.addView(grayDot1)
        dotsHolder.addView(grayDot2)
        dotsHolder.addView(grayDot3)
        walkingHolder.addView(dotsHolder)

        if (distance.isNotEmpty()) {
            //Distance Text
            val distanceView = TextView(detailedContext)
            distanceView.text = distance
            distanceView.setTextSize(12f)
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

    //Just pass in the specific direction
    private fun createBusComponent(direction: Direction) {

        val busNum: Int? = direction.busNumber
        val startTime = direction.startTime
        val endTime = direction.endTime

    }

}