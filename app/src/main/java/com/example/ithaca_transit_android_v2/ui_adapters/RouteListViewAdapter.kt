package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
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
import com.example.ithaca_transit_android_v2.views.DrawRouteCard
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// Recycler view adapter that fills each route card with the list of Route objects that is returned by our RouteOptions networking call.
class RouteListViewAdapter(context: Context, var userList: ArrayList<RouteListAdapterObject>) :
    RecyclerView.Adapter<RouteListViewAdapter.ViewHolder>() {

    var routeCardContext = context

    override fun getItemViewType(position: Int): Int {
        if (userList[position].type.equals("routeLabel")) {
            return R.layout.route_label
        } else {
            return R.layout.route_cards
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(viewType, p0, false)
        v.invalidate()

        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun drawRouteCard(p0: ViewHolder, p1: Int) {

        p0.directionList.removeAllViews()
        p0.busDrawing.removeAllViews()

        val routeObj: Route = userList[p1].data as Route

        //Are there walking directions
        val containsWalking: Boolean =
            routeObj.directions[routeObj.directions.size - 1].type == DirectionType.WALK

        //Temporary info to fill the routecard so that we can see the difference between cards.
        val boardMins = routeObj.boardInMin.toString()
        p0.description?.text = "Board in " + boardMins + " Mins"
        p0.delay.text = "On Time"

        val summaryList: ArrayList<String> = ArrayList()
        val busList: ArrayList<Int> = ArrayList()

        for (i in 0 until routeObj.routeSummary!!.size) {

            routeObj.routeSummary.get(i).stopName?.let { summaryList.add(it) }

            //Check if bus number is null, if not add it to the list
            if (routeObj.routeSummary.get(i).direction?.busNumber != null) {
                routeObj.routeSummary.get(i).direction?.busNumber?.let { busList.add(it) }
            }

        }

        val lDirectionparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lDirectionparams.weight = 1f

        val busIconParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val walkingIconParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val linearlayoutparams = p0.directionList.layoutParams
        val busImageLayoutParam = p0.busDrawing.layoutParams
        val dotParams = p0.dotDrawing.layoutParams

        Log.d("SummaryListSize", "" + summaryList.size)

        if (containsWalking) {
            if (summaryList.size > 3) {
                linearlayoutparams.height = 400
                dotParams.height = 400


                if (summaryList.size == 4) {
                    p0.dotDrawing.setBlueDimensions(20f, 215f, 195f)
                    p0.dotDrawing.setGrayDimensions(20f, 255f, 325f, 30f)

                } else if (summaryList.size == 5) {
                    p0.dotDrawing.setBlueDimensions(20f, 255f, 235f)
                    p0.dotDrawing.setGrayDimensions(20f, 302f, 345f, 0f)

                } else {
                    p0.dotDrawing.setBlueDimensions(20f, 255f, 235f)
                    p0.dotDrawing.setGrayDimensions(20f, 260f, 310f, 22f)
                }

                //Forces the canvas to redraw and update dots
                p0.dotDrawing.invalidate()

            } else {
                linearlayoutparams.height = 300
                dotParams.height = 300
                p0.dotDrawing.setBlueDimensions(20f, 115f, 95f)
                p0.dotDrawing.setGrayDimensions(20f, 160f, 220f, 22f)
                //var drawRouteCard=DrawRouteCard(routeCardContext, null, 36f, 222f, 180f )

            }
        }
        else if (!containsWalking){
            if (summaryList.size > 2) {
                linearlayoutparams.height = 400
                dotParams.height = 400


                if (summaryList.size == 3) {
                    p0.dotDrawing.setBlueDimensions(20f, 325f, 305f, false)

                } else {
                    p0.dotDrawing.setBlueDimensions(20f, 255f, 235f, false)
                }

                //Forces the canvas to redraw and update dots
                p0.dotDrawing.invalidate()

            } else {
                linearlayoutparams.height = 200
                dotParams.height = 200
                p0.dotDrawing.setBlueDimensions(20f, 120f, 100f, false)
                //var drawRouteCard=DrawRouteCard(routeCardContext, null, 36f, 222f, 180f )

            }
        }

            for (direction in summaryList) {
                val individualDirection = TextView(routeCardContext)
                individualDirection.text = direction
                p0.directionList.addView(individualDirection)
                individualDirection.textSize = 15f
                individualDirection.layoutParams = lDirectionparams
            }

        //Create bus number images
        for (i in busList) {

            val busNumberView = BusNumberComponent(
                routeCardContext,
                null
            )
            busNumberView.setBusNumber(i)
            busIconParams.weight = 1f
            busIconParams.gravity = Gravity.CENTER

            //Changing margins based on how many buses
            if (busList.size >= 3) {

                busIconParams.bottomMargin = 25
            } else if (busList.size == 2) {
                busIconParams.topMargin = 10
                busIconParams.bottomMargin = 35
            } else {
                busIconParams.topMargin = 10
                busIconParams.bottomMargin = 20
            }

            //Set the busParams
            busNumberView.layoutParams = busIconParams
            p0.busDrawing.addView(busNumberView)

        }

        p0.directionList.invalidate()
        //Add walking image
        val walkingView = ImageView(routeCardContext)

        // Set an image for ImageView
        walkingView.setImageDrawable(
            ContextCompat.getDrawable(
                routeCardContext, // Context
                R.drawable.walking_vector // Drawable
            )
        )

        //Changing margins based on how many buses
        if (busList.size == 2) {
            walkingIconParams.topMargin = 30
        } else if (busList.size >= 3) {
            walkingIconParams.topMargin = 15
        } else {
            walkingIconParams.topMargin = 30
        }
        walkingIconParams.weight = 1f
        walkingView.layoutParams = walkingIconParams

        //Only add walking if there is walking direction
        if (containsWalking) {
            p0.busDrawing.addView(walkingView)
        }

        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        val timeText: String = sdf.format(routeObj.depart) + " - " + sdf.format(routeObj.arrival)

        //set route duration
        p0.routeDuration.text = timeText
        p0.routeDuration.setTypeface(null, Typeface.BOLD);

        //Create list of directions

        p0.directionList.layoutParams = linearlayoutparams
        p0.directionList.invalidate()
        p0.busDrawing.invalidate()
        p0.dotDrawing.invalidate()
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        if (getItemViewType(p1) == R.layout.route_cards) {
            drawRouteCard(p0, p1)
        } else {
            val label = userList[p1].data as String
            p0.routeLabel.text = label
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.board_minutes)
        val delay = itemView.findViewById<TextView>(R.id.delay_label)
        val directionList = itemView.findViewById<LinearLayout>(R.id.directions)
        val dotDrawing = itemView.findViewById<DrawRouteCard>(R.id.drawingDots)
        val busDrawing = itemView.findViewById<LinearLayout>(R.id.icons)
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