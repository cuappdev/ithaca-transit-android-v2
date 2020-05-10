package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.media.Image
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Direction
import com.example.ithaca_transit_android_v2.ui_adapters.ExpandedStopsAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.exp

class BusExpandable(context: Context, direction: Direction) : LinearLayout(context) {

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

    private var expandableTop: LinearLayout
    private var expandableRecycler : RecyclerView
    private var direction = direction
    private var trimmedStops = direction.busStops.subList(1,direction.busStops.size-1)

    val diffTimeMillis = direction.endTime.time - direction.startTime.time
    val diffTimeMins = TimeUnit.MINUTES.convert(diffTimeMillis, TimeUnit.MILLISECONDS).toInt()

    init {
        LinearLayout.inflate(context, R.layout.expandable_bus_stops, this)

        expandableTop = findViewById(com.example.ithaca_transit_android_v2.R.id.expanded_top)



        val expandedTop = createExpandableTop()

        expandableTop.addView(expandedTop)

        expandableRecycler = findViewById(R.id.expanded_stops)
        expandableRecycler.apply {
            layoutManager = LinearLayoutManager(detailedContext)
            adapter = ExpandedStopsAdapter(detailedContext, trimmedStops)
        }

        expandableTop.setOnClickListener{
            if(expandableRecycler.visibility == View.VISIBLE){
                expandableRecycler.visibility = View.GONE
            }
            else{
                expandableRecycler.visibility = View.VISIBLE
            }


        }
//        val expandableRecyclerParams = ViewGroup.MarginLayoutParams(
//            RecyclerView.LayoutParams.MATCH_PARENT,
//            RecyclerView.LayoutParams.WRAP_CONTENT
//        )
//        expandableRecyclerParams.leftMargin = BUS_LEFT_MARGIN
//
//        expandableRecycler.layoutParams = expandableRecyclerParams


    }


    private fun createExpandableTop(): LinearLayout {

        //Linear Layout that holds the top part of expandable
        val topHolder = LinearLayout(detailedContext)
        topHolder.orientation = LinearLayout.HORIZONTAL


        val topHolderParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        topHolderParams.leftMargin = BUS_LEFT_MARGIN

        topHolder.layoutParams = topHolderParams

        //Add line to top
        //Adding set params because line seems to stretch forever
        val directionLineParams = ViewGroup.MarginLayoutParams(
            10,
            100
        )
        //directionLineParams.leftMargin = BUS_LEFT_MARGIN
        val directionLine = DirectionLine(detailedContext, "blue", 100f, 8f)

        directionLine.layoutParams = directionLineParams

        //Add Stops Top
        val stopsText = LinearLayout(detailedContext)
        stopsText.orientation = LinearLayout.HORIZONTAL

        val stopsTextParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        stopsTextParams.leftMargin = DESCRIPTION_LEFT_MARGIN
        stopsTextParams.topMargin = 20
        stopsText.layoutParams = stopsTextParams

        val numStops = direction.busStops.size - 2

        //First Text
        //TODO Add spannable elements
        val firstDescription = TextView(detailedContext)

        firstDescription.text = "" + numStops + " stops"

        firstDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.gray))
        val descriptionParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        descriptionParams.leftMargin = 10
        firstDescription.layoutParams = descriptionParams

        stopsText.addView(firstDescription)

        //SmallGray dot
        val smallSpaceDot: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        smallSpaceDot.leftMargin = 10
        smallSpaceDot.rightMargin = 10
        smallSpaceDot.topMargin = 20

        val smallDot: ImageView = ImageView(detailedContext)
        smallDot.setImageResource(R.drawable.ic_oval5)

        smallDot.layoutParams = smallSpaceDot

        stopsText.addView(smallDot)

        //Second Part of text
        val secondDescription = TextView(detailedContext)

        secondDescription.text = "" + diffTimeMins + " mins"

        secondDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.gray))
        val descriptionParams2: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //descriptionParams2.leftMargin = 10
        //descriptionParams2.topMargin =
        secondDescription.layoutParams = descriptionParams2

        stopsText.addView(secondDescription)

        val smallDownArrowParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        smallDownArrowParams.leftMargin = 12
        smallDownArrowParams.topMargin = 20

        val smallDownArrow: ImageView = ImageView(detailedContext)
        smallDownArrow.setImageResource(R.drawable.ic_downarrow)

        smallDownArrow.layoutParams = smallDownArrowParams

        stopsText.addView(smallDownArrow)

        //lineHolder.setBackgroundColor(ContextCompat.getColor(detailedContext, R.color.gray))

        topHolder.addView(directionLine)
        topHolder.addView(stopsText)

        return topHolder
    }

}