package com.cornellappdev.android.ithaca_transit.views

import CenterSpan
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.cornellappdev.android.ithaca_transit.R
import com.cornellappdev.android.ithaca_transit.models.Direction
import java.util.concurrent.TimeUnit


class BusExpandable(context: Context, direction: Direction, timeMargin: Int) : LinearLayout(context) {

    val DESCRIPTION_LEFT_MARGIN = 60
    var EXPANDABLE_MARGIN: Int = 0
    var detailedContext = context

    private var expandableTop: LinearLayout
    private var expandableLinearLayout : LinearLayout
    private var direction = direction
    private var trimmedStops = direction.busStops.subList(1, direction.busStops.size - 1)

    val diffTimeMillis = direction.endTime.time - direction.startTime.time
    val diffTimeMins = TimeUnit.MINUTES.convert(diffTimeMillis, TimeUnit.MILLISECONDS).toInt()

    init {
        inflate(context, R.layout.expandable_bus_stops, this)

        EXPANDABLE_MARGIN = timeMargin

        expandableTop = findViewById(R.id.expanded_top)

        val expandedTop = createExpandableTop()

        expandableTop.addView(expandedTop)

        expandableLinearLayout = findViewById(R.id.expandedstops_layout)

        for(i in trimmedStops.indices){
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

    private fun createStops(position: Int) : LinearLayout {
        val expandedHolder = LinearLayout(detailedContext)
        expandedHolder.orientation = VERTICAL
        expandedHolder.gravity = Gravity.CENTER_VERTICAL
        val holderParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //Stop dots are offset by 12, seems universal across systems
        holderParams.leftMargin = EXPANDABLE_MARGIN - 12
        expandedHolder.layoutParams = holderParams

        //Create Dot
        val dotDirectionLayout = LinearLayout(detailedContext)
        dotDirectionLayout.orientation = HORIZONTAL
        dotDirectionLayout.gravity = Gravity.CENTER_VERTICAL
        val params = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dotDirectionLayout.layoutParams = params
        val radius = 16f

        val size: Int = (radius * 2).toInt()

        val verticalPadding = 10f
        val dot = DirectionDot(
            detailedContext,
            colorStr = "blue",
            useNestedCircles = true,
            drawSegmentAbove = true,
            drawSegmentBelow = true,
            radius = radius,
            lineWidth = 8f,
            verticalPadding = verticalPadding,
            drawOutline = true
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
//        stopName.setLineSpacing(0f, 0f)
//        stopName.includeFontPadding = false
        stopName.layoutParams = stopNameParams

        dotDirectionLayout.addView(stopName)

        //Add DirectionLine
        val directionLineParams = ViewGroup.MarginLayoutParams(
            10,
            50
        )
        //Have to makeup for the 12 leftMargin in the holderParams
        directionLineParams.leftMargin = 12
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

        //Subtract first and last
        val numStops = direction.busStops.size - 2
        val numStopsText: String = if(numStops != 1) {
            "$numStops stops"
        } else {
            "$numStops stop"
        }
        val spanString: Spannable = SpannableString("$numStopsText   $diffTimeMins min")
        spanString.setSpan(CenterSpan(detailedContext, R.drawable.ic_oval5), numStopsText.length + 1,
            numStopsText.length + 2, DynamicDrawableSpan.ALIGN_BASELINE)
        val tripSummary = TextView(detailedContext)
        val descriptionParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        descriptionParams.leftMargin = 10
        tripSummary.layoutParams = descriptionParams
        tripSummary.text = spanString
        tripSummary.setTextColor(ContextCompat.getColor(detailedContext, R.color.gray))
        stopsText.addView(tripSummary)

        val smallDownArrowParams: MarginLayoutParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        smallDownArrowParams.leftMargin = 12
        smallDownArrowParams.topMargin = 20

        val smallDownArrow = ImageView(detailedContext)
        smallDownArrow.setImageResource(R.drawable.ic_downarrow)

        smallDownArrow.layoutParams = smallDownArrowParams

        stopsText.addView(smallDownArrow)

        topHolder.addView(directionLine)
        topHolder.addView(stopsText)

        return topHolder
    }

}