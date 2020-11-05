package com.example.ithaca_transit_android_v2.ui_adapters

import CenterSpan
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Direction
import com.example.ithaca_transit_android_v2.models.DirectionType
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.views.*
import kotlinx.android.synthetic.main.route_detailed_holder.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class RouteDetailAdapter(var context: Context, _routeDetail: View) {

    val routeDetail: View = _routeDetail
    val detailedLayout: LinearLayout = routeDetail.detailed_dynamic_layout

    val TOP_MARGIN = 15
    val TIME_LEFT_MARGIN = 50
    val DOTS_LEFT_MARGIN = 25
    val DESCRIPTION_LEFT_MARGIN = 60
    val DISTANCE_TOP_MARGIN = 20
    val SMALLDOT_TOP_MARGIN = 5
    var TIME_TEXT_WIDTH = 0
    //How can we generalize this arbitrary 12?
    var SMALLDOT_LEFT_MARGIN = TIME_LEFT_MARGIN + DOTS_LEFT_MARGIN + 12
    var DIRECTION_LINE_MARGIN = TIME_LEFT_MARGIN + DOTS_LEFT_MARGIN + 12
    var stopTextViewId = View.generateViewId()
    var detailedContext = context
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())

    //Create Views for Times
    private fun createTimeText(time: String): TextView {
        val textView = TextView(detailedContext)
        textView.text = time
        textView.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
        val timeParams: ViewGroup.MarginLayoutParams =
            ViewGroup.MarginLayoutParams(TIME_TEXT_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT)
        timeParams.leftMargin = TIME_LEFT_MARGIN
        timeParams.rightMargin = TIME_TEXT_WIDTH/5
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
        //Layout that holds dots and stop names
        val dotDirectionLayout = LinearLayout(detailedContext)
        dotDirectionLayout.orientation = LinearLayout.HORIZONTAL
        dotDirectionLayout.gravity = Gravity.CENTER_VERTICAL
        val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = DOTS_LEFT_MARGIN
        if(drawSegmentAbove) {
            params.topMargin = -TOP_MARGIN
        }
        dotDirectionLayout.layoutParams = params

        val timeText = createTimeText(time)

        dotDirectionLayout.addView(timeText)

        // Setting Dot Radius
        var radius = 16f
        if (isFinalDestination) {
            //radius = 20f
            radius = 18f
            //Bigger radius offsets the final destination dot, have to move it back slightly.
            params.leftMargin = params.leftMargin - 2
        }

        val colorStr: String = if (directionType == DirectionType.BUS) {
            "blue"
        } else {
            "gray"
        }
        val verticalPadding = 18f

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
            busTextLayout.layoutParams = paramsBus


//            //Use spannable to see how many lines is spanned by text.
//            val spanString: Spannable = SpannableString("$movementDescription   at $destination")
//            spanString.setSpan(ImageSpan(detailedContext, R.drawable.bus_vector), movementDescription.length + 1,
//                movementDescription.length + 2, DynamicDrawableSpan.ALIGN_BOTTOM)
//            val stopText = TextView(detailedContext)
//            stopText.text = spanString
//            val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            stopText.layoutParams = descriptionParams
//            busTextLayout.addView(stopText)
//            Log.d("spanAdapter", stopText.lineCount.toString())
//            Log.d("spanAdapter", spanString.lines().size.toString())

            val firstDescription = TextView(detailedContext)

            firstDescription.text = movementDescription

            firstDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
            val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            firstDescription.layoutParams = descriptionParams

            busTextLayout.addView(firstDescription)

            //Bus image in text
            val busIconParams = ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            busIconParams.leftMargin = 7
            busIconParams.rightMargin = 7

            val busNumberView = BusNumberComponent(detailedContext, R.layout.bus_image)
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
            stopTextViewId = View.generateViewId()
            val secondDescription = TextView(detailedContext)
            secondDescription.id = stopTextViewId

            secondDescription.text = "at $destination"

            secondDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
            val descriptionParams2: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            secondDescription.layoutParams = descriptionParams2

//            secondDescription.maxLines = 1
//            secondDescription.isSingleLine = true
//            secondDescription.ellipsize = TextUtils.TruncateAt.MARQUEE
//            secondDescription.marqueeRepeatLimit = -1
//            secondDescription.isFocusableInTouchMode = true
//            secondDescription.isSelected = true

            busTextLayout.addView(secondDescription)
            dotDirectionLayout.addView(busTextLayout)
        } else if (expandedBottom) {
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
        return dotDirectionLayout
    }

    private fun makeSmallDot(color: String): View {
        val dotParams = ViewGroup.MarginLayoutParams(32, 27)
        dotParams.topMargin = 6
        val dot = DirectionDot(
            context = detailedContext,
            colorStr = color,
            useNestedCircles = false,
            drawSegmentBelow = false,
            drawSegmentAbove = false,
            radius = 6f,
            lineWidth = 0f,
            verticalPadding = 0f
        )
        dot.layoutParams = dotParams
        return dot
    }

    private fun createWalkingComponent(distance: String, color: String = "gray"): LinearLayout {
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

        val grayDot1 = makeSmallDot(color)
        val grayDot2 = makeSmallDot(color)
        val grayDot3 = makeSmallDot(color)
        dotsHolder.addView(grayDot1)
        dotsHolder.addView(grayDot2)
        dotsHolder.addView(grayDot3)
        walkingHolder.addView(dotsHolder)

        if (distance.isNotEmpty()) {
            //Distance Text
            val distanceView = TextView(detailedContext)
            distanceView.text = distance
            distanceView.textSize = 12f
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

    // Bases the rest of the time margins off of the width of the first time in the direction
    private fun adjustTimeMargin(time: String) {
        val timeMarginView = TextView(detailedContext)
        timeMarginView.text = time
        val timeParams: ViewGroup.MarginLayoutParams =
            ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        timeParams.leftMargin = TIME_LEFT_MARGIN
        timeMarginView.layoutParams = timeParams
        timeMarginView.measure(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        SMALLDOT_LEFT_MARGIN -= TIME_TEXT_WIDTH + TIME_TEXT_WIDTH/5
        DIRECTION_LINE_MARGIN -= TIME_TEXT_WIDTH + TIME_TEXT_WIDTH/5
        TIME_TEXT_WIDTH = timeMarginView.measuredWidth
        SMALLDOT_LEFT_MARGIN += TIME_TEXT_WIDTH + TIME_TEXT_WIDTH/5
        DIRECTION_LINE_MARGIN += TIME_TEXT_WIDTH + TIME_TEXT_WIDTH/5


//        var radius = 16f
//        val colorStr: String = "blue"
//        val verticalPadding = 18f
//
//        val num = View.generateViewId()
//
//        //Initialize Dots (Done indiviudally)
//        val dot = DirectionDot(
//            detailedContext, colorStr, true, true,
//            true, radius, 8f, verticalPadding
//        )
//        val size: Int = (radius * 2).toInt()
//        //Create layout params for dots
//        val canvasParams: ViewGroup.LayoutParams =
//            ViewGroup.LayoutParams(size, size + 2 * verticalPadding.toInt())
//        dot.layoutParams = canvasParams
//        dot.id = stopTextViewId
//
//        //dot.measure(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
//        detailedLayout.addView(dot)
//        detailedLayout.findViewById<DirectionDot>(stopTextViewId).measure(
//            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
//            ViewGroup.MarginLayoutParams.WRAP_CONTENT
//        )
//
//        detailedLayout.findViewById<DirectionDot>(stopTextViewId).viewTreeObserver.addOnGlobalLayoutListener(
//            object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    // make sure it is not called anymore
//                    detailedLayout.findViewById<DirectionDot>(stopTextViewId).viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    Log.d(
//                        "adapterDot",
//                        detailedLayout.findViewById<DirectionDot>(stopTextViewId).measuredWidth.toString()
//                    )
//                    detailedLayout.removeView(dot)
//                }
//            })
    }

    private fun updateTypeIsWalking(direction: Direction, route: Route, i: Int) {
        val distance = "" + direction.distance.toInt() + " ft"
        val time: String
        if(i != route.directions.lastIndex) {
            time = sdf.format(direction.startTime)
        } else {
            time = sdf.format(direction.endTime)
            detailedLayout.addView(createWalkingComponent(distance))
        }
        detailedLayout.addView(
            createDirectionLinearLayout(
                time,
                "Walk to",
                direction.name,
                direction.type,
                drawSegmentAbove = false,
                drawSegmentBelow = false,
                isFinalDestination = i == route.directions.lastIndex
            )
        )
        // Want the dots to appear AFTER the direction linear layout is created as opposed to before
        // if final destination
        if(i!=route.directions.lastIndex) detailedLayout.addView(createWalkingComponent(distance))
    }

    private fun updateTypeIsBus(direction: Direction, route: Route, i: Int) {
        if(direction.busStops.isNotEmpty()) {
            val busLinearLayout =
                direction.routeNumber?.let {
                    createDirectionLinearLayout(
                        sdf.format(direction.startTime),
                        "Board",
                        direction.name,
                        direction.type,
                        drawSegmentAbove = i > 0 && route.directions[i - 1].type == DirectionType.BUS,
                        drawSegmentBelow = true,
                        isFinalDestination = false,
                        busNumber = it
                    )
                }
            detailedLayout.addView(busLinearLayout)
            // Only create bus expandable if there isn't just a start and stop destination
            if(direction.busStops.size > 2) {
                val expandedTop = BusExpandable(detailedContext, direction, SMALLDOT_LEFT_MARGIN)

                val expandedTopParams = ViewGroup.MarginLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                expandedTop.layoutParams = expandedTopParams

                detailedLayout.addView(expandedTop)
            } else {
                val directionLineParams = ViewGroup.MarginLayoutParams(8, 100)
                directionLineParams.leftMargin = DIRECTION_LINE_MARGIN
                val directionLine = DirectionLine(detailedContext, "blue", 100f, 8f)
                directionLine.layoutParams = directionLineParams
                detailedLayout.addView(directionLine)
            }
            val bottomExpanded = createDirectionLinearLayout(
                sdf.format(direction.endTime),
                "Get off at",
                direction.busStops.last().name,
                direction.type,
                drawSegmentAbove = true,
                drawSegmentBelow = i < route.directions.lastIndex && route.directions[i + 1].type == DirectionType.BUS,
                isFinalDestination = i == route.directions.lastIndex,
                expandedBottom = true
            )
            detailedLayout.addView(bottomExpanded)
            if(i < route.directions.lastIndex && route.directions[i + 1].type == DirectionType.BUS) {
                //Add DirectionLine
                val directionLineParams = ViewGroup.MarginLayoutParams(
                    8,
                    100
                )
                directionLineParams.leftMargin = DIRECTION_LINE_MARGIN
                directionLineParams.topMargin = 0
                val directionLine = DirectionLine(detailedContext, "blue", 100f, 8f)
                directionLine.layoutParams = directionLineParams
                detailedLayout.addView(directionLine)
                //detailedLayout.addView(createWalkingComponent("", "blue"))
                //Does a route direction like this exist? A string of buses then an intermediary walk?
            } else if(i < route.directions.lastIndex - 1 && route.directions[i + 1].type == DirectionType.WALK) {
                detailedLayout.addView(createWalkingComponent(""))
            }
            //mitchell @ college
        }
    }

    //Creates a header, adds respective icon by direction type + initial information about route
    private fun createHeader(route: Route) {
        val directions = route.directions
        val iconView = routeDetail.findViewById<LinearLayout>(R.id.route_detail_icon_holder)
        val routeDetailHeader = routeDetail.findViewById<TextView>(R.id.route_detail_header)
        val tripDurationTextView = routeDetail.findViewById<TextView>(R.id.route_detail_trip_duration)
        iconView.removeAllViews()

        val tripDuration = route.arrival.time - route.depart.time
        val hours = TimeUnit.MILLISECONDS.toHours(tripDuration).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(tripDuration) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                tripDuration
            )
        )).toInt()
        var durationString = ""
        if(hours >= 1) {
            durationString += "$hours hr"
            if(hours > 1) {
                durationString += "s"
            }
            if(minutes >= 0) {
                durationString += " and "
            }
        }
        if(minutes >= 0) {
            durationString += "$minutes min"
        }
        if(minutes != 1) {
            durationString += "s"
        }
        if(minutes == 0 && hours == 0) {
            durationString = "0 minutes"
        }
        tripDurationTextView.text = context.getString(R.string.trip_duration, durationString)
        if(directions.size == 1 && directions[0].type == DirectionType.WALK) {
            val busIconParams = ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val walkingView = WalkingManComponent(detailedContext, R.layout.walking_image)
            walkingView.layoutParams = busIconParams
            iconView.addView(walkingView)
            routeDetailHeader.text = HtmlCompat.fromHtml(
                context.getString(
                    R.string.walk_to,
                    route.endDestination
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        } else {
            var firstBusDirection: Direction? = null
            for(direction in directions) {
                if(direction.type == DirectionType.BUS) {
                    firstBusDirection = direction
                    break
                }
            }
            if(firstBusDirection?.routeNumber != null) {
                //Bus image
                val busIconParams = ViewGroup.MarginLayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val busNumberView = BusNumberComponent(
                    detailedContext,
                    R.layout.bus_image_route_detail
                )
                busNumberView.setBusNumber(firstBusDirection.routeNumber!!)
                busNumberView.layoutParams = busIconParams
                iconView.addView(busNumberView)
                routeDetailHeader.text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.depart_at,
                        sdf.format(firstBusDirection.startTime),
                        firstBusDirection.name
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }

    fun updateRouteDetail(route: Route) {
        detailedLayout.removeAllViews()

        val isOnlyWalking = route.directions.size == 1 && route.directions[0].type == DirectionType.WALK
        val directions = route.directions

        createHeader(route)

        adjustTimeMargin(sdf.format(directions[0].startTime))

        for( i in route.directions.indices) {
            val direction = directions[i]
            //Walking component go before or after?
            if(direction.type == DirectionType.WALK && !isOnlyWalking) {
                updateTypeIsWalking(direction, route, i)
            }
            if (direction.type == DirectionType.BUS) {
                updateTypeIsBus(direction, route, i)
            }
        }
        //Handle case if the one and only direction is walking
        if(isOnlyWalking) {
            val directionLayout = createDirectionLinearLayout(
                sdf.format(route.arrival),
                "Walk to",
                route.endDestination,
                directionType = DirectionType.WALK,
                drawSegmentAbove = false,
                drawSegmentBelow = false,
                isFinalDestination = true
            )
            detailedLayout.addView(directionLayout)
        }
    }
}