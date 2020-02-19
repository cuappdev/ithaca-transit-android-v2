package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.models.directionSummary
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

// Recycler view adapter that fills each route card with the list of Route objects that is returned by our RouteOptions networking call.
class RvAdapter(val userList: ArrayList<Route>, context: Context) :
    RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    var routeCardContext = context

    //Creation of the observable object, defining that it will hold a RouteCardState
    private val clickSubject = PublishSubject.create<RouteCardState>()
    val clickEvent: Observable<RouteCardState> = clickSubject

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.routecards, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        //Temporary info to fill the routecard so that we can see the difference between cards.
        p0.description?.text = userList[p1].arrival.toString()
        p0.delay?.text = userList[p1].boardInMin.toString()

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
        val linearlayoutparams = p0.directionList.layoutParams
        val busImageLayoutParam = p0.busDrawing.layoutParams
        val dotParams = p0.dotDrawing.layoutParams

        if (summaryList.size > 3) {
            linearlayoutparams.height = 400
            dotParams.height = 400
            p0.dotDrawing.setDimensions(20f, 345f, 325f)

            //Forces the canvas to redraw and update dots
            p0.dotDrawing.invalidate()

        } else {
            linearlayoutparams.height = 300
            p0.dotDrawing.setDimensions(20f, 220f, 180f)
            //var drawRouteCard=DrawRouteCard(routeCardContext, null, 36f, 222f, 180f )

        }

        p0.directionList.layoutParams = linearlayoutparams

        for (i in summaryList) {

            var individualDirection = TextView(routeCardContext)
            individualDirection.setText(i)

            p0.directionList.addView(individualDirection)
            lDirectionparams.weight = 1f

            individualDirection.textSize = 15f

            individualDirection.layoutParams = lDirectionparams

        }



        for (i in busList) {

            var busNumberView = busNumberView(routeCardContext, null)
            busNumberView.setBusNumber(i)
            p0.busDrawing.addView(busNumberView)

        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.route_description)
        val delay = itemView.findViewById<TextView>(R.id.delay)
        val directionList = itemView.findViewById<LinearLayout>(R.id.directions)
        val dotDrawing = itemView.findViewById<DrawRouteCard>(R.id.drawingDots)
        val busDrawing = itemView.findViewById<LinearLayout>(R.id.icons)
        //val customBusView = itemView.findViewById<FrameLayout>(R.id.busFrameLayout)
        val dotLayout = itemView.findViewById<LinearLayout>(R.id.path_dots)
        val cardViewDelegate = itemView.findViewById<CardView>(R.id.cardviewdelegate)

        init {
            //Listener for onClicks - creates observable with Route object corresponding to clicked routeCard
            itemView.setOnClickListener {
                clickSubject.onNext(RouteDetailViewState(userList[layoutPosition]))
            }
        }
    }
}