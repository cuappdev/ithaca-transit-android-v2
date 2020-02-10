package com.example.ithaca_transit_android_v2.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.route_card_compact.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList

class RouteCardCompact : AppCompatActivity() {

    val routeCardContext = this

    var rvAdapter = RvAdapter(ArrayList())
    private lateinit var searchDisposable: Disposable

    var dataList = ArrayList<Route>()


    //Temporary data used for networking calls.
    val end = Coordinate(42.45352923315714, -76.4802676615646)
    val uid = "E4A0256E-5865-4E9F-8A5A-33747CAC7EBF"
    val time = 1581308199.0
    val destinationName = "Bill & Melinda Gates Hall"
    val start = Coordinate(42.447319055865485, -76.48542509781194)
    val arriveBy = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_card_compact)

        runBlocking {
            val loc1 = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Balch")
            }.await()


            val loc2 = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Olin")
            }.await()


            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getRouteOptions(
                    loc1[0].coordinate,
                    loc2[0].coordinate,
                    time,
                    false,
                    "yeet"
                )
            }.await()


            dataList = ArrayList(deferred.boardingSoon)


            val recyclerView = findViewById<RecyclerView>(R.id.nearby_stops_routes)
            recyclerView.layoutManager =
                LinearLayoutManager(routeCardContext, RecyclerView.VERTICAL, false)


            rvAdapter = RvAdapter(dataList)
            recyclerView.adapter = rvAdapter


        }


        val bottom_sheet = findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)


        /*
        * Logic that handles the where the routecardview will go when user slides
        */
        var slideState = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {


                val location = IntArray(2)
                bottom_sheet.getLocationOnScreen(location)

                /*
                * */
                if (location[1] > 600 && location[1] < 1000) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
                }

            }

            override fun onStateChanged(@NonNull view: View, i: Int) {
                when (i) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        slideState = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        slideState = BottomSheetBehavior.STATE_EXPANDED
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> slideState =
                        BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }

        })

        val routePresenter = RouteCardPresenter(bottomSheet, routeCardContext)

        searchDisposable = routePresenter.initRouteCardView(rvAdapter.clickEvent)

    }


    //Destroy observables when application is closed
    override fun onDestroy() {
        super.onDestroy()
        searchDisposable?.dispose()
    }
}