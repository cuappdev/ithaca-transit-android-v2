package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.media.Image
import android.util.Log
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
import kotlinx.android.synthetic.main.item_searchview.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.exp

class BusExpandable(context: Context, direction: Direction, timeMargin: Int) : LinearLayout(context) {

    val TIME_LEFT_MARGIN = 50
    val TIME_TEXT_WIDTH = 200
    val DOTS_LEFT_MARGIN = 25
    val DESCRIPTION_LEFT_MARGIN = 60
    val EXPANDABLE_MARGIN = TIME_LEFT_MARGIN + DOTS_LEFT_MARGIN + TIME_TEXT_WIDTH + 10
    val DIRECTION_LINE_ID = View.generateViewId()
    var detailedContext = context

    private var expandableTop: LinearLayout
    private var expandableLinearLayout : LinearLayout
    private var direction = direction
    private var trimmedStops = direction.busStops.subList(1,direction.busStops.size-1)

    val diffTimeMillis = direction.endTime.time - direction.startTime.time
    val diffTimeMins = TimeUnit.MINUTES.convert(diffTimeMillis, TimeUnit.MILLISECONDS).toInt()

    init {
        inflate(context, R.layout.expandable_bus_stops, this)

        expandableTop = findViewById(R.id.expanded_top)

        val expandedTop = createExpandableTop()

        expandableTop.addView(expandedTop)

        expandableLinearLayout = findViewById(R.id.expandedstops_layout)

        for(i in 0 until trimmedStops.size){
            val stop = createStops(i)
            expandableLinearLayout.addView(stop)
        }

        expandableLinearLayout.visibility = View.GONE

        expandableTop.setOnClickListener{
            if(expandableLinearLayout.visibility == View.VISIBLE){
                expandableLinearLayout.visibility = View.GONE
            } else{
                expandableLinearLayout.visibility = View.VISIBLE
            }
        }
    }

    //Stop dots offset by 12, idk why, big gap, idk why.
    private fun createStops(position : Int) : LinearLayout {
        val expandedHolder = LinearLayout(detailedContext)
        expandedHolder.orientation = VERTICAL
        expandedHolder.gravity = Gravity.CENTER_VERTICAL
        val holderParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        holderParams.leftMargin = EXPANDABLE_MARGIN
        expandedHolder.layoutParams = holderParams

        //Create Dot
        val dotDirectionLayout = LinearLayout(detailedContext)
        dotDirectionLayout.orientation = HORIZONTAL
        dotDirectionLayout.gravity = Gravity.CENTER_VERTICAL
        val params = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = -12
        dotDirectionLayout.layoutParams = params
        val radius = 16f

        val size: Int = (radius * 2).toInt()

        val verticalPadding = 10f
        val dot = DirectionDot(
            detailedContext, colorStr = "blue", useNestedCircles = true, drawSegmentAbove = true,
            drawSegmentBelow = true, radius = radius, lineWidth = 8f, verticalPadding = verticalPadding
        )
        val canvasParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(size, size + 2 * verticalPadding.toInt())
        dot.layoutParams = canvasParams
        dotDirectionLayout.addView(dot)

        //Stop Name
        val stopNameText = trimmedStops.get(position).name
        val stopName = TextView(detailedContext)
        stopName.text = stopNameText
        stopName.setTextColor(ContextCompat.getColor(detailedContext, R.color.gray))
        val stopNameParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        stopNameParams.leftMargin = DESCRIPTION_LEFT_MARGIN
        stopName.layoutParams = stopNameParams

        dotDirectionLayout.addView(stopName)

        //Add DirectionLine
        val directionLineParams = ViewGroup.MarginLayoutParams(
            10,
            50
        )
        val directionLine = DirectionLine(detailedContext, "blue", 50f, 8f)

        directionLine.layoutParams = directionLineParams

        expandedHolder.addView(dotDirectionLayout)
        expandedHolder.addView(directionLine)

        return expandedHolder
    }

    private fun createExpandableTop(): LinearLayout {

        //Linear Layout that holds the top part of expandable
        val topHolder = LinearLayout(detailedContext)
        topHolder.orientation = LinearLayout.HORIZONTAL


        val topHolderParams = ViewGroup.MarginLayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        topHolderParams.leftMargin = EXPANDABLE_MARGIN

        topHolder.layoutParams = topHolderParams

        //Add line to top
        //Adding set params because line seems to stretch forever
        val directionLineParams = MarginLayoutParams(
            10,
            100
        )
        //directionLineParams.leftMargin = BUS_LEFT_MARGIN
        val directionLine = DirectionLine(detailedContext, "blue", 100f, 8f)

        directionLine.layoutParams = directionLineParams
        //directionLine.id = DIRECTION_LINE_ID

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
        smallSpaceDot.topMargin = 25

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

        val smallDownArrow = ImageView(detailedContext)
        smallDownArrow.setImageResource(R.drawable.ic_downarrow)

        smallDownArrow.layoutParams = smallDownArrowParams

        stopsText.addView(smallDownArrow)

        //lineHolder.setBackgroundColor(ContextCompat.getColor(detailedContext, R.color.gray))

        topHolder.addView(directionLine)
        topHolder.addView(stopsText)

        return topHolder
    }

}