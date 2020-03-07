package com.example.ithaca_transit_android_v2.ui_adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import com.example.ithaca_transit_android_v2.views.BusNumberComponent
import com.example.ithaca_transit_android_v2.views.DrawRouteCard
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

// Recycler view adapter that fills each route card with the list of Route objects that is returned by our RouteOptions networking call.
class RouteViewAdapter(context: Context, var userList: ArrayList<Route>) :
    RecyclerView.Adapter<RouteViewAdapter.ViewHolder>() {

    var routeCardContext = context

    //Creation of the observable object, defining that it will hold a RouteCardState
    private val clickSubject = PublishSubject.create<RouteCardState>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.routecards, p0, false)
        v.invalidate()

        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.directionList.removeAllViews()
        p0.busDrawing.removeAllViews()


        //Temporary info to fill the routecard so that we can see the difference between cards.
        val boardMins = userList[p1].boardInMin.toString()
        p0.description?.setText("Board in " + boardMins + " Mins")
        p0.delay.setText("On Time")

        var summaryList: ArrayList<String> = ArrayList()
        var busList: ArrayList<Int> = ArrayList()

        for (i in 0 until userList[p1].routeSummary!!.size) {

            userList[p1].routeSummary?.get(i)?.stopName?.let { summaryList.add(it) }

            //Check if bus number is null, if not add it to the list
            if (userList[p1].routeSummary?.get(i)?.direction?.busNumber != null) {
                userList[p1].routeSummary?.get(i)?.direction?.busNumber?.let { busList.add(it) }
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







        for (i in summaryList) {

            var individualDirection = TextView(routeCardContext)
            individualDirection.setText(i)

            p0.directionList.addView(individualDirection)

            individualDirection.textSize = 15f

            individualDirection.layoutParams = lDirectionparams

        }

        //Create bus number images
        for (i in busList) {

            var busNumberView = BusNumberComponent(
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




        p0.busDrawing.addView(walkingView)

        fun convertMillis(mil: Long): String {
            var s: Long = mil / 1000
            s = (s%(3600*24))
            val m: Long = s/60
            var h: Int = (m/60).toInt()
            var mins: Long = m%60


            if (h > 12) {
                h = h - 12
            }

            return String.format("%d:%02d", h, mins)
        }
        //Set route duration

        val arrivalTime = convertMillis(userList[p1].arrival.time).toString()
        Log.d("timesAConvert", ""+userList[p1].arrival.time)
        Log.d("timesAConvert", ""+userList[p1].arrival)
        Log.d("timesAConvertConvert", arrivalTime)
        val departTime = convertMillis(userList[p1].depart.time).toString()
        Log.d("timesDConvert", departTime)
        Log.d("timesD", ""+userList[p1].depart)


        p0.routeDuration.setText(departTime + " - " + arrivalTime)

        p0.routeDuration.setTypeface(null, Typeface.BOLD);

        //Create list of directions
        Log.d("SummaryListSize", "im here")
        p0.directionList.layoutParams = linearlayoutparams
        Log.d("SummaryListSizeD", ""+p0.directionList.layoutParams.height)
        Log.d("SummaryListSizeBus", ""+p0.busDrawing.layoutParams.height)
        Log.d("SummaryListSizeDot", ""+p0.dotDrawing.layoutParams.height)
        p0.directionList.invalidate()
        p0.busDrawing.invalidate()
        p0.dotDrawing.invalidate()

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.route_description)
        val delay = itemView.findViewById<TextView>(R.id.delay)
        val directionList = itemView.findViewById<LinearLayout>(R.id.directions)
        val dotDrawing = itemView.findViewById<DrawRouteCard>(R.id.drawingDots)
        val busDrawing = itemView.findViewById<LinearLayout>(R.id.icons)
        val routeDuration = itemView.findViewById<TextView>(R.id.duration)
        //val customBusView = itemView.findViewById<FrameLayout>(R.id.busFrameLayout)
        val dotLayout = itemView.findViewById<LinearLayout>(R.id.path_dots)
        val cardViewDelegate = itemView.findViewById<CardView>(R.id.cardviewdelegate)

        init {
            //Listener for onClicks - creates observable with Route object corresponding to clicked routeCard
            itemView.setOnClickListener {
                clickSubject.onNext(RouteDetailViewState(userList[layoutPosition]))


                val millis = userList[layoutPosition].arrival.time

            }
        }
    }

    fun swapItems(updatedInfo: ArrayList<Route>) {


        this.userList = updatedInfo
        notifyDataSetChanged()
    }
}