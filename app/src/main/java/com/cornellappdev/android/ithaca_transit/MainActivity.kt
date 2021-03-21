package com.cornellappdev.android.ithaca_transit

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.android.ithaca_transit.models.Location
import com.cornellappdev.android.ithaca_transit.presenters.MapPresenter
import com.cornellappdev.android.ithaca_transit.presenters.RouteOptionsPresenter
import com.cornellappdev.android.ithaca_transit.presenters.SearchPresenter
import com.cornellappdev.android.ithaca_transit.ui_adapters.RouteDetailAdapter
import com.cornellappdev.android.ithaca_transit.ui_adapters.SearchViewAdapter

import com.cornellappdev.android.ithaca_transit.util.CurrLocationManager
import com.cornellappdev.android.ithaca_transit.ui_adapters.RouteListViewAdapter

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.routes_holder.*
import kotlinx.android.synthetic.main.route_detailed_holder.*

import kotlinx.android.synthetic.main.search_main.*
import kotlinx.android.synthetic.main.search_secondary.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var searchDisposable: Disposable
    private lateinit var routeCardDisposable: Disposable

    private var mSearchLocations: List<Location> = ArrayList()
    private lateinit var mSearchAdapter: SearchViewAdapter
    private lateinit var mRouteListViewAdapter: RouteListViewAdapter
    private lateinit var mRouteDetailAdapter: RouteDetailAdapter
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
        setContentView(R.layout.activity_main);

        mSearchAdapter = SearchViewAdapter(this, mSearchLocations)
        mRouteListViewAdapter =
            RouteListViewAdapter(this, ArrayList())
        mRouteDetailAdapter = RouteDetailAdapter(this, route_detailed_holder)
        mSearchPresenter = SearchPresenter(search_card_holder, map_fragment as MapFragment,this, mSearchAdapter)
        mRouteOptionsPresenter = RouteOptionsPresenter(bottom_sheet, mRouteListViewAdapter, mRouteDetailAdapter, this)

        mRouteOptionsPresenter.setBottomSheetCallback(bottom_sheet)

        searchDisposable = mSearchPresenter.initSearchView()
        routeCardDisposable = mRouteOptionsPresenter.initRouteCardView();

        routeCardDisposable = mRouteOptionsPresenter.initRouteCardView();

        // set up search adapter, location_list refers to listview of locations on launch
        // location_list_2 refers to the listview of locations when editing their route options
        locations_list.adapter = mSearchAdapter
        change_locations_list.adapter = mSearchAdapter

        boarding_soon_routes.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        boarding_soon_routes.adapter = mRouteListViewAdapter
        boarding_soon_routes.isNestedScrollingEnabled = true

        initializeLocationManager()
        //fetchRouteData()

    }

    fun initializeLocationManager() {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mCurrLocationManager = CurrLocationManager(this, locationManager, this)
    }

    /* When user grants privileges to the user, initialize the location manager */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 888) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initializeLocationManager()
            }

        }
    }
}

