package com.example.ithaca_transit_android_v2.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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


// Class that handles the bottom sliding sheet.
class RouteCardCompact : AppCompatActivity() {

    val routeCardContext = this

    var rvAdapter = RvAdapter(ArrayList())
    private lateinit var routeDisposable: Disposable

    var dataList = ArrayList<Route>()

    //Temporary data used for networking calls.
    val time = 1581308199.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_card_compact)

        // Temporary networking calls that is used to get start and end locations.
        runBlocking {
            val startLoc = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Balch")
            }.await()
            val endLoc = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Olin")
            }.await()
            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getRouteOptions(
                    startLoc[0].coordinate,
                    endLoc[0].coordinate,
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
                * Tells the slider to snap to the middle when the user lifts their finger near the middle of the screen.
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

        routeDisposable = routePresenter.initRouteCardView(rvAdapter.clickEvent)

    }

    //Destroy observables when application is closed
    override fun onDestroy() {
        super.onDestroy()
        routeDisposable?.dispose()
    }
}