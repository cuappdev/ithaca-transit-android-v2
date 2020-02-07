package com.example.ithaca_transit_android_v2.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.NetworkUtils
import com.example.ithaca_transit_android_v2.R
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.presenters.RouteCardPresenter
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.route_card_compact.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList

class RouteCardCompact : AppCompatActivity(){

    val c = this

    var rvAdapter = RvAdapter(ArrayList())
    private lateinit var searchDisposable: Disposable

    private var subscribe: Disposable? = null
    var dataList = ArrayList<Route>()


    val end = Coordinate(42.45352923315714,-76.4802676615646)
    val uid = "E4A0256E-5865-4E9F-8A5A-33747CAC7EBF"
    val time = 1580887917.0
    val destinationName = "Bill & Melinda Gates Hall"
    val start = Coordinate(42.447319055865485,-76.48542509781194)
    val arriveBy = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_card_compact)



        val fab = findViewById<RelativeLayout>(R.id.fab)
        val routeOptionsText = findViewById<TextView>(R.id.optionsText)



        runBlocking {
            val loc1 = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Jameson")
            }.await()

            Log.v("loc1", loc1.toString())


            val loc2 = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Statler")
            }.await()


            Log.v("loc2", loc2.toString())

            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getRouteOptions(end, start, time, false, "yeet")
            }.await()

            // Printing out [deferred] in log for testing

            dataList = ArrayList(deferred.boardingSoon)


            val recyclerView = findViewById<RecyclerView>(R.id.nearby_stops_routes)
            recyclerView.layoutManager= LinearLayoutManager(c, RecyclerView.VERTICAL, false)


            rvAdapter= RvAdapter(dataList)
            recyclerView.adapter = rvAdapter



        }




        val bottom_sheet = findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)



        var state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                //Log.d("qwerty", "true")


                val location = IntArray(2)
                bottom_sheet.getLocationOnScreen(location)

                if(location[1] > 600 && location[1]< 1000){
                    Log.d("inSlide", state.toString())
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
                }

                Log.d("qwerty2", location[1].toString())




            }

            override fun onStateChanged(@NonNull view: View, i: Int) {
                when (i) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
//                    BottomSheetBehavior.STATE_DRAGGING -> if (state != BottomSheetBehavior.STATE_HALF_EXPANDED) {
//                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
//                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> state =
                        BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }

        })



        val routePresenter = RouteCardPresenter(bottomSheet, c)


//        }


        searchDisposable = routePresenter.initRouteCardView(rvAdapter.clickEvent)


    }


//    private fun setupRecyclerView() {
//
//        val recyclerView = findViewById<RecyclerView>(R.id.nearby_stops_routes)
//        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//
//        recyclerView.adapter = rvAdapter
//
//    }


//    fun setupItemClick() {
//        routePresenter.initRouteCardView(rvAdapter.clickEvent)
//    }



    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }
}