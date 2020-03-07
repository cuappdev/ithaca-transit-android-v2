package com.example.ithaca_transit_android_v2

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.presenters.MapPresenter
import com.example.ithaca_transit_android_v2.presenters.SearchPresenter
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter

import com.example.ithaca_transit_android_v2.util.CurrLocationManager

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.search_main.*
import kotlinx.android.synthetic.main.search_secondary.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var searchDisposable: Disposable
    private var mSearchLocations: List<Location> = ArrayList()
    private lateinit var mSearchAdapter: SearchViewAdapter
    private lateinit var mSearchPresenter: SearchPresenter
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
        mSearchAdapter = SearchViewAdapter(this, mSearchLocations)
<<<<<<< HEAD

        mSearchPresenter = SearchPresenter(search_card_holder, this, mSearchAdapter)

        searchDisposable = mSearchPresenter.initSearchView()
//        (map_fragment as SupportMapFragment).getMapAsync(this)
=======
        mSearchPresenter = SearchPresenter(search_card_holder, this, mSearchAdapter)

        searchDisposable = mSearchPresenter.initSearchView()
>>>>>>> master

        // set up search adapter, location_list refers to listview of locations on launch
        // location_list_2 refers to the listview of locations when editing their route options
        locations_list.adapter = mSearchAdapter
        locations_list_2.adapter = mSearchAdapter
        initializeLocationManager()
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

