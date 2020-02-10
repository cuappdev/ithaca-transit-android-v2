package com.example.ithaca_transit_android_v2.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RvAdapter(val userList: ArrayList<Route>) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    //Creation of the observable object, defining that it will hold a RouteCardState
    private val clickSubject = PublishSubject.create<RouteCardState>()
    val clickEvent: Observable<RouteCardState> = clickSubject


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.routecards, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.description?.text = userList[p1].arrival.toString()
        p0.delay?.text = userList[p1].boardInMin.toString()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.route_description)
        val delay = itemView.findViewById<TextView>(R.id.delay)


        init {
            //Listener for onClicks - creates observable with Route object corresponding to clicked routeCard
            itemView.setOnClickListener {
                clickSubject.onNext(RouteDetailViewState(userList[layoutPosition]))
            }
        }
    }
}