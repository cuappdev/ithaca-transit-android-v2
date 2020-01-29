package com.example.ithaca_transit_android_v2.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
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
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList

class RouteCardCompact : AppCompatActivity(){

    val routePresenter = RouteCardPresenter(this)
    var rvAdapter = RvAdapter(ArrayList())



    private var subscribe: Disposable? = null
    var dataList = ArrayList<Route>()



    val end = Coordinate(42.45352923315714,-76.4802676615646)
    val uid = "E4A0256E-5865-4E9F-8A5A-33747CAC7EBF"
    val time = 1580262349.879257
    val destinationName = "Bill & Melinda Gates Hall"
    val start = Coordinate(42.447319055865485,-76.48542509781194)
    val arriveBy = false

    val c = this







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_card_compact)


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
                NetworkUtils().getRouteOptions(loc1[0], loc2[0], time)
            }.await()

            // Printing out [deferred] in log for testing

            dataList = ArrayList(deferred.boardingSoon)

            val recyclerView = findViewById<RecyclerView>(R.id.nearby_stops_routes)
            recyclerView.layoutManager = LinearLayoutManager(c, RecyclerView.VERTICAL, false)

            rvAdapter= RvAdapter(dataList)
            recyclerView.adapter = rvAdapter

        }








        val bottom_sheet = findViewById<RelativeLayout>(R.id.relative_layout)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        val button = findViewById<Button>(R.id.halfButton)

        halfButton.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        setupItemClick()




    }


//    private fun setupRecyclerView() {
//
//        val recyclerView = findViewById<RecyclerView>(R.id.nearby_stops_routes)
//        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//
//        recyclerView.adapter = rvAdapter
//
//    }



    fun setupItemClick() {
        routePresenter.determineState(rvAdapter.clickEvent)
    }



    override fun render(state: RouteCardState) {

    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }
}