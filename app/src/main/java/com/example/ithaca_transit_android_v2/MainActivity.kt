package com.example.ithaca_transit_android_v2

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.presenters.MapPresenter
import com.example.ithaca_transit_android_v2.presenters.RouteOptionsPresenter
import com.example.ithaca_transit_android_v2.presenters.SearchPresenter
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter
import com.example.ithaca_transit_android_v2.util.CompositeOnItemClickListener

import com.example.ithaca_transit_android_v2.util.CurrLocationManager
import com.example.ithaca_transit_android_v2.ui_adapters.RouteViewAdapter

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.route_card_compact.*

import kotlinx.android.synthetic.main.search_main.*
import kotlinx.android.synthetic.main.search_secondary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var searchDisposable: Disposable
    private lateinit var routeCardDisposable: Disposable

    private var mSearchLocations: List<Location> = ArrayList()
    private var dataList = ArrayList<Route>()
    private lateinit var mSearchAdapter: SearchViewAdapter
    private lateinit var mRouteViewAdapter: RouteViewAdapter
    private lateinit var mSearchPresenter: SearchPresenter
    private lateinit var mRouteOptionsPresenter: RouteOptionsPresenter
    private lateinit var mCurrLocationManager: CurrLocationManager

    override fun onMapReady(map: GoogleMap?) {
        val p = MapPresenter()
        if (map == null) {
            //TODO: Display error state map failed
        } else {
            p.initMapView(map)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Repository.destinationListListeners = CompositeOnItemClickListener()
        Repository.changeRouteListeners = CompositeOnItemClickListener()

        mSearchAdapter = SearchViewAdapter(this, mSearchLocations)
        mRouteViewAdapter =
            RouteViewAdapter(this, ArrayList())
        mSearchPresenter = SearchPresenter(search_card_holder, this, mSearchAdapter)
        mRouteOptionsPresenter = RouteOptionsPresenter(bottomSheet, mRouteViewAdapter)
        mRouteOptionsPresenter.setBottomSheetCallback(BottomSheetBehavior.from(bottomSheet), bottomSheet);

        searchDisposable = mSearchPresenter.initSearchView()
        routeCardDisposable = mRouteOptionsPresenter.initRouteCardView();

        // set up search adapter, location_list refers to listview of locations on launch
        // location_list_2 refers to the listview of locations when editing their route options
        locations_list.adapter = mSearchAdapter
        change_locations_list.adapter = mSearchAdapter

        nearby_stops_routes.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        nearby_stops_routes.adapter = mRouteViewAdapter

        locations_list.setOnItemClickListener(Repository.destinationListListeners)
        change_locations_list.setOnItemClickListener(Repository.changeRouteListeners)

        initializeLocationManager()
        fetchRouteData()

    }

    fun fetchRouteData() {
        runBlocking {
            val startLoc = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Balch")
            }.await()
            val endLoc = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("Olin")
            }.await()
            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getRouteOptions(
                    startLoc[1].coordinate,
                    endLoc[0].coordinate,
                    1582150074.0,
                    false,
                    "Final Destination"
                )
            }.await()

            dataList = ArrayList(deferred.boardingSoon)

        }
    }

    fun initializeLocationManager() {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mCurrLocationManager = CurrLocationManager(this, locationManager, this)
    }

    override fun onStop() {
        super.onStop()
        if (!searchDisposable.isDisposed) {
            searchDisposable.dispose()
        }
        if (!routeCardDisposable.isDisposed) {
            routeCardDisposable.dispose()
        }
    }

    /* When user grants privileges to the user, initialize the location manager */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 888) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initializeLocationManager()
            }

        }
    }
}

