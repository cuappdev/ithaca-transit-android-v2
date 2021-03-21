package com.cornellappdev.android.ithaca_transit.ui_adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.android.ithaca_transit.R
import com.cornellappdev.android.ithaca_transit.models.Stop
import com.cornellappdev.android.ithaca_transit.views.DirectionDot
import com.cornellappdev.android.ithaca_transit.views.DirectionLine

class ExpandedStopsAdapter(context: Context, var busStops: List<Stop>) :

    RecyclerView.Adapter<ExpandedStopsAdapter.ViewHolder>() {

    val TOP_MARGIN = 15
    val TIME_LEFT_MARGIN = 50
    val TIME_RIGHT_MARGIN = 50
    val DOTS_LEFT_MARGIN = 25
    val BUS_ICON_LEFT_MARGIN = 50
    val WALKING_ICON_LEFT_MARGIN = 92
    val DESCRIPTION_LEFT_MARGIN = 60
    val DISTANCE_TOP_MARGIN = 5
    val BUS_LEFT_MARGIN = TIME_LEFT_MARGIN + TIME_RIGHT_MARGIN + 155
    val SMALLDOT_LEFT_MARGIN = TIME_LEFT_MARGIN + TIME_RIGHT_MARGIN + 165
    val SMALLDOT_TOP_MARGIN = 20

    val expandedContext = context

    //stops should not include the first and last stop
    val stops: List<Stop> = busStops

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutHolder = itemView.findViewById<LinearLayout>(R.id.expandedStops_holder)



    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.stops_layout

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(viewType, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return stops.size
    }

    override fun onBindViewHolder(p0: ViewHolder, position: Int) {
        drawStops(p0, position)

    }

    private fun drawStops(p0: ViewHolder, p1: Int) {
        p0.layoutHolder.removeAllViews()

        val expandedHolder = LinearLayout(expandedContext)
        expandedHolder.orientation = LinearLayout.VERTICAL
        expandedHolder.gravity = Gravity.CENTER_VERTICAL
        val holderParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        holderParams.leftMargin = BUS_LEFT_MARGIN
        expandedHolder.layoutParams = holderParams

        //Create Dot
        val dotDirectionLayout = LinearLayout(expandedContext)
        dotDirectionLayout.orientation = LinearLayout.HORIZONTAL
        dotDirectionLayout.gravity = Gravity.CENTER_VERTICAL
        val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dotDirectionLayout.layoutParams = params

        var radius = 16f

        val size: Int = (radius * 2).toInt()

        val verticalPadding = 10f

        //TODO FIX DOT PARAMETERS
        val dot = DirectionDot(
            expandedContext, "blue", true, true,
            true, radius, 8f, verticalPadding
        )

        val canvasParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(size, size + 2 * verticalPadding.toInt())
        dot.layoutParams = canvasParams

        dotDirectionLayout.addView(dot)

        //Stop Name
        val stopNameText = stops.get(p1).name
        val stopName = TextView(expandedContext)
        stopName.text = stopNameText
        stopName.setTextColor(ContextCompat.getColor(expandedContext, R.color.gray))
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
        //directionLineParams.leftMargin = BUS_LEFT_MARGIN
        val directionLine = DirectionLine(expandedContext, "blue", 50f, 8f)
        directionLineParams.leftMargin = 12

        directionLine.layoutParams = directionLineParams

        expandedHolder.addView(dotDirectionLayout)
        expandedHolder.addView(directionLine)
        p0.layoutHolder.addView(expandedHolder)

    }

}