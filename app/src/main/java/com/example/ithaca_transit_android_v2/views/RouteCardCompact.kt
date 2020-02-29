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
import kotlinx.android.synthetic.main.search_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList

// Class that handles the bottom sliding sheet.
class RouteCardCompact : AppCompatActivity() {

    val routeCardContext = this

    val strtCoord = Coordinate(42.45352923315714, -76.4802676615646)
    val endCoord = Coordinate(42.4557, -76.4782)

    var rvAdapter = RvAdapter(ArrayList(), routeCardContext)
    private lateinit var routeDisposable: Disposable

    var dataList = ArrayList<Route>()

    //Temporary data used for networking calls.
    val time = 1582754826.0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_card_compact)

        // Temporary networking calls that is used to get start and end locations.
        runBlocking {
            val startLoc = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Jameson")
            }.await()
            val endLoc = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Statler")
            }.await()
            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getRouteOptions(
                    strtCoord,
                    endCoord,
                    time,
                    false,
                    "Final Destination"
                )
            }.await()

            dataList = ArrayList(deferred.boardingSoon)

            val recyclerView = findViewById<RecyclerView>(R.id.nearby_stops_routes)
            recyclerView.layoutManager =
                LinearLayoutManager(routeCardContext, RecyclerView.VERTICAL, false)


            rvAdapter = RvAdapter(dataList, routeCardContext)
            recyclerView.adapter = rvAdapter

        }

        val bottom_sheet = findViewById<LinearLayout>(R.id.bottomSheet)

        /*
        * Logic that handles the where the routecardview will go when user slides
        */
        var slideState = BottomSheetBehavior.STATE_COLLAPSED


        //val routePresenter = RouteCardPresenter(search_card_holder, bottomSheet)

       // routeDisposable = routePresenter.initRouteCardView()

    }

    //Destroy observables when application is closed
    override fun onDestroy() {
        super.onDestroy()
    }
}