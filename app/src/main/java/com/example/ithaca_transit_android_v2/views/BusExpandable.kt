package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.media.Image
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Direction
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.exp

class BusExpandable (context: Context, direction: Direction) : LinearLayout(context) {

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


    private var expandableTop : LinearLayout
    private var direction = direction

    val diffTimeMillis =  direction.endTime.time - direction.startTime.time
    val diffTimeMins = TimeUnit.MINUTES.convert(diffTimeMillis, TimeUnit.MILLISECONDS).toInt()



    init {
        LinearLayout.inflate(context, R.layout.expandable_bus_stops, this)

        expandableTop = findViewById(com.example.ithaca_transit_android_v2.R.id.expanded_top)

        val expandedTop = createExpandableTop()

        expandableTop.addView(expandedTop)

    }



    private fun createExpandableTop() : LinearLayout {

        //Linear Layout that holds the top part of expandable
        val topHolder = LinearLayout(detailedContext)
        topHolder.orientation = LinearLayout.HORIZONTAL

        val topHolderParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        topHolder.layoutParams = topHolderParams




        //Add line to top
        val directionLineParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        directionLineParams.leftMargin = BUS_LEFT_MARGIN
        val directionLine = DirectionLine(detailedContext, "blue", 100f, 8f)
        directionLine.layoutParams = directionLineParams



        //Add Stops Top
        val stopsText = LinearLayout(detailedContext)
        stopsText.orientation = LinearLayout.HORIZONTAL

        val stopsTextParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        stopsTextParams.leftMargin = 100
        stopsTextParams.topMargin = 20
        stopsText.layoutParams = stopsTextParams

        val numStops = direction.busStops.size - 2

        //First Text
        //TODO Add spannable elements
        val firstDescription = TextView(detailedContext)

        firstDescription.text = ""+numStops + " stops"

        firstDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
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
        smallSpaceDot.leftMargin = 7
        smallSpaceDot.rightMargin = 7
        smallSpaceDot.topMargin = 20

        val smallDot : ImageView = ImageView(detailedContext)
        smallDot.setImageResource(R.drawable.ic_oval5)

        smallDot.layoutParams = smallSpaceDot

        stopsText.addView(smallDot)

        //Second Part of text
        val secondDescription = TextView(detailedContext)

        secondDescription.text = ""+diffTimeMins + " mins"

        secondDescription.setTextColor(ContextCompat.getColor(detailedContext, R.color.black))
        val descriptionParams2: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //descriptionParams2.leftMargin = 10
        //descriptionParams2.topMargin =
        secondDescription.layoutParams = descriptionParams2

        stopsText.addView(secondDescription)

        stopsText.setBackgroundColor(ContextCompat.getColor(detailedContext, R.color.gray))

        topHolder.addView(directionLine)
        topHolder.addView(stopsText)


        return topHolder
    }




}