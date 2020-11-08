package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.DirectionType
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.views.BusNumberComponent
import com.example.ithaca_transit_android_v2.views.DirectionDot
import com.example.ithaca_transit_android_v2.views.DirectionLine
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// Recycler view adapter that fills each route card with the list of Route objects that is returned by our RouteOptions networking call.
class RouteListViewAdapter(context: Context, var userList: ArrayList<RouteListAdapterObject>) :
    RecyclerView.Adapter<RouteListViewAdapter.ViewHolder>() {

    val DOTS_LEFT_MARGIN = 240
    val BUS_ICON_LEFT_MARGIN = 50
    val WALKING_ICON_LEFT_MARGIN = 92
    val DESCRIPTION_LEFT_MARGIN = 40
    val DISTANCE_TOP_MARGIN = 5
    var routeCardContext = context

    override fun getItemViewType(position: Int): Int {
        if (userList[position].type.equals("routeLabel")) {
            return R.layout.route_label
        } else {
            return R.layout.route_card
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(viewType, p0, false)
        v.invalidate()
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    //Creates views for dots and stop names
    private fun createDirectionLinearLayout(
        description: String,
        isBusStop: Boolean,
        //Boolean values to fill in segments between two blue dots
        drawSegmentAbove: Boolean,
        drawSegmentBelow: Boolean,
        //Boolean to handle textview distance
        isDestination: Boolean
    ): LinearLayout {

        //Layout that holds dots and stop names
        val dotDirectionLayout = LinearLayout(routeCardContext)
        dotDirectionLayout.orientation = LinearLayout.HORIZONTAL
        dotDirectionLayout.gravity = Gravity.CENTER_VERTICAL
        val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = DOTS_LEFT_MARGIN
        dotDirectionLayout.layoutParams = params

        // Setting Dot Radius
        var radius = 16f
        if (isDestination) {
            radius = 20f
            params.leftMargin = params.leftMargin - 4
        }

        val colorStr: String = if (isBusStop) {
            "blue"
        } else {
            "gray"
        }
        val verticalPadding = 10f

        //Initialize Dots (Done indiviudally)
        val dot = DirectionDot(
            routeCardContext, colorStr, isDestination, drawSegmentAbove,
            drawSegmentBelow, radius, 8f, verticalPadding
        )
        val size: Int = (radius * 2).toInt()

        //Create layout params for dots
        val canvasParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(size, size + 2 * verticalPadding.toInt())
        dot.layoutParams = canvasParams
        dotDirectionLayout.addView(dot)

        //Stop Name
        val descriptionView = TextView(routeCardContext)
        descriptionView.text = description
        descriptionView.setTextColor(ContextCompat.getColor(routeCardContext, R.color.black))
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
        val walkingHolder = LinearLayout(routeCardContext)
        walkingHolder.orientation = LinearLayout.HORIZONTAL

        val walkingIconParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        walkingIconParams.leftMargin = WALKING_ICON_LEFT_MARGIN
        val walkingView = ImageView(routeCardContext)
        walkingView.setImageDrawable(
            ContextCompat.getDrawable(routeCardContext, R.drawable.walking_vector)
        )
        walkingView.layoutParams = walkingIconParams

        val walkingMarginHolder = LinearLayout(routeCardContext)
        val walkingMarginHolderParams = ViewGroup.LayoutParams(
            DOTS_LEFT_MARGIN,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        walkingMarginHolder.gravity = Gravity.START
        walkingMarginHolder.layoutParams = walkingMarginHolderParams
        walkingMarginHolder.addView(walkingView)
        walkingHolder.addView(walkingMarginHolder)

        val dotsHolder = LinearLayout(routeCardContext)
        dotsHolder.orientation = LinearLayout.VERTICAL

        val grayDotParams = ViewGroup.MarginLayoutParams(32, 18)
        grayDotParams.leftMargin = 10
        grayDotParams.topMargin = 6

        val grayDot1 = DirectionDot(routeCardContext, "gray", false, false, false, 6f, 0f, 0f)
        grayDot1.layoutParams = grayDotParams
        dotsHolder.addView(grayDot1)

        val grayDot2 = DirectionDot(
            routeCardContext, "gray", false,
            false, false, 6f, 0f, 0f
        )
        grayDot2.layoutParams = grayDotParams
        dotsHolder.addView(grayDot2)

        walkingHolder.addView(dotsHolder)

        if (hasDistance) {
            //Distance Text
            val distanceView = TextView(routeCardContext)
            distanceView.text = distance
            distanceView.setTextSize(10f)
            distanceView.setTextColor(ContextCompat.getColor(routeCardContext, R.color.gray))
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

    private fun createBusIconComponent(busNumber: String): View {

        val busHolder = LinearLayout(routeCardContext)
        busHolder.orientation = LinearLayout.HORIZONTAL

        val busIconParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        busIconParams.leftMargin = BUS_ICON_LEFT_MARGIN
        val busNumberView = BusNumberComponent(routeCardContext)
        busNumberView.setBusNumber(busNumber)
        busNumberView.layoutParams = busIconParams

        val busIconHolder = LinearLayout(routeCardContext)
        val busIconHolderParams = ViewGroup.LayoutParams(
            DOTS_LEFT_MARGIN,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        busIconHolder.layoutParams = busIconHolderParams
        busIconHolder.addView(busNumberView)
        busHolder.addView(busIconHolder)

        val directionLineParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        directionLineParams.leftMargin = 12
        val directionLine = DirectionLine(routeCardContext, "blue", 100f, 8f)
        directionLine.layoutParams = directionLineParams

        busHolder.addView(directionLine)
        return busHolder
    }

    private fun drawRouteCard(p0: ViewHolder, p1: Int) {
        p0.routeDynamicList.removeAllViews()
        val routeObj: Route = userList[p1].data as Route

        val boardHours = routeObj.boardInMin / 60
        val boardMins = routeObj.boardInMin % 60
        var timeString = "in "
        if(boardHours >= 1) {
            timeString += "$boardHours hr"
            if(boardMins >= 0) {
                timeString += " "
            }
        }
        if(boardMins >= 0) {
            timeString += "$boardMins min"
        }
        if(boardMins == 0 && boardHours == 0) {
            timeString = "now"
        }
        p0.description?.text = routeCardContext.getString(R.string.board_in_mins, timeString)
        p0.delay.text = routeCardContext.getString(R.string.on_time)

        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        val routeInterval: String =
            sdf.format(routeObj.depart) + " - " + sdf.format(routeObj.arrival)
        p0.routeDuration.text = routeInterval
        p0.routeDuration.setTypeface(null, Typeface.BOLD)

        val isOnlyWalking =
            routeObj.directions.size == 1 && routeObj.directions[0].type == DirectionType.WALK
        var isStopDestinationName = true
        for(i in routeObj.directions.indices) {
            val direction = routeObj.directions[i]
            if(direction.type == DirectionType.WALK) {
                //Adds a walking component given whatever distance is in the direction object
                var distance = "" + direction.distance.toInt() + " ft"
                if(isOnlyWalking) {
                    distance = "" + BigDecimal(routeObj.travelDistance).setScale(
                        2,
                        RoundingMode.HALF_EVEN
                    ) + " mi"
                }
                val walkingImageView = createWalkingComponent(distance, true)
                //Only considers drawing a direction linear layout from whatever starting location if
                //the destination is at the start / is only walking
                if(i == 0 || isOnlyWalking) {
                    Repository.startLocation?.name?.let {
                        val directionLayout = createDirectionLinearLayout(
                            it,
                            isBusStop = false,
                            drawSegmentAbove = false,
                            drawSegmentBelow = false,
                            isDestination = false
                        )
                        p0.routeDynamicList.addView(directionLayout)
                    }
                }
                p0.routeDynamicList.addView(walkingImageView)
            }
            if (i > 0 || direction.type == DirectionType.BUS) {
                val stopName = direction.name
                val isBusRoute = direction.type == DirectionType.BUS
                val directionLayout = createDirectionLinearLayout(
                    stopName,
                    isBusStop = isBusRoute,
                    drawSegmentAbove =
                        i > 0 && routeObj.directions[i - 1].type == DirectionType.BUS && stopName != routeObj.endDestination,
                    drawSegmentBelow = isBusRoute,
                    isDestination = stopName == routeObj.endDestination)
                p0.routeDynamicList.addView(directionLayout)
                if(isBusRoute && direction.routeId != null) {
                    val busImageView = createBusIconComponent(direction.routeId)
                    p0.routeDynamicList.addView(busImageView)
                }
                if(direction.busStops.isNotEmpty()) {
                    val busStop = direction.busStops.last()
                    //Only considers adding the last bus stop to the view if it isn't the name
                    //of the next direction / isn't the end destination
                    if(i < routeObj.directions.lastIndex && busStop.name != routeObj.directions[i+1].name) {
                        p0.routeDynamicList.addView(
                            createDirectionLinearLayout(
                                busStop.name,
                                isBusStop = true,
                                drawSegmentAbove = true,
                                drawSegmentBelow = i != routeObj.directions.lastIndex - 1,
                                isDestination = false
                            )
                        )
                    //This considers if the destination happens to be the last stop of the current
                    //direction
                    } else if(i == routeObj.directions.lastIndex) {
                        if(busStop.name != routeObj.endDestination) isStopDestinationName = false
                        p0.routeDynamicList.addView(
                            createDirectionLinearLayout(
                                busStop.name,
                                isBusStop = true,
                                drawSegmentAbove = true,
                                drawSegmentBelow = false,
                                isDestination = i == routeObj.directions.lastIndex && isStopDestinationName
                            )
                        )
                    }
                }
            }
        }
        // Creates end destination layout for route that's just walking, hides boarding text if so
        // Also creates an empty walking component to the destination if last stop wasn't destination
        if(isOnlyWalking || !isStopDestinationName) {
            if(!isStopDestinationName) p0.routeDynamicList.addView(createWalkingComponent("", false))
            val directionLayout = createDirectionLinearLayout(
                routeObj.endDestination,
                isBusStop = false,
                drawSegmentAbove = false,
                drawSegmentBelow = false,
                isDestination = true
            )
            p0.routeDynamicList.addView(directionLayout)
            if(isOnlyWalking) {
                p0.description.visibility = View.GONE
                p0.delay.visibility = View.GONE
            }
        }
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if (getItemViewType(p1) == R.layout.route_card) {
            drawRouteCard(p0, p1)
        } else {
            val label = userList[p1].data as String
            p0.routeLabel.text = label
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.board_minutes)
        val delay = itemView.findViewById<TextView>(R.id.delay_label)
        val routeDynamicList = itemView.findViewById<LinearLayout>(R.id.route_dynamic_list)
        val routeDuration = itemView.findViewById<TextView>(R.id.duration)

        val routeLabel = itemView.findViewById<TextView>(R.id.routeLabel)

        init {
            //Listener for onClicks - creates observable with Route object corresponding to clicked routeCard
            itemView.setOnClickListener { _ ->
                if (userList[layoutPosition].data is Route) {
                    Repository._updateRouteDetailed(userList[layoutPosition].data as Route)
                }
            }
        }
    }

    fun swapItems(updatedInfo: ArrayList<RouteListAdapterObject>) {
        this.userList = updatedInfo
        notifyDataSetChanged()
    }
}