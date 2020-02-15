package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.models.directionSummary
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

// Recycler view adapter that fills each route card with the list of Route objects that is returned by our RouteOptions networking call.
class RvAdapter(val userList: ArrayList<Route>, context: Context) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

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

        var summaryList : ArrayList<String> = ArrayList()

        for(i in 0 until userList[p1].routeSummary!!.size){
            userList[p1].routeSummary?.get(i)?.stopName?.let { summaryList.add(it) }
        }
        Log.d("Divider", summaryList.size.toString())
        if(summaryList.size > 3){
            Log.d("Divider", summaryList.size.toString())
            p0.directionList.dividerHeight = -40
        }

        var directionsAdapter = ArrayAdapter<String>(
            routeCardContext, android.R.layout.simple_list_item_1, summaryList

        )
        p0.directionList.adapter = directionsAdapter

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.route_description)
        val delay = itemView.findViewById<TextView>(R.id.delay)
        val directionList = itemView.findViewById<ListView>(R.id.directions)

        init {
            //Listener for onClicks - creates observable with Route object corresponding to clicked routeCard
            itemView.setOnClickListener {
                clickSubject.onNext(RouteDetailViewState(userList[layoutPosition]))
            }
        }
    }
}