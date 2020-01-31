package com.example.ithaca_transit_android_v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.presenters.SearchPresenter
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar_search.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var searchDisposable: Disposable
    private var mSearchLocations: List<Location> = ArrayList()
    private var mSearchAdapter: SearchViewAdapter? = null
    private lateinit var mSearchPresenter: SearchPresenter

    override fun onMapReady(map: GoogleMap?) {
        map!!.setOnMapClickListener { point ->
            Log.i("qwerty", "map clicked")
            search_view.clearFocus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSearchPresenter = SearchPresenter(search_view, this)
        mSearchAdapter = SearchViewAdapter(this, mSearchLocations)
        locations_list.adapter = mSearchAdapter
        locations_list.setOnItemClickListener { parent, view, position, id ->
            val destination = parent.getItemAtPosition(position) as Location

            val intent = Intent(this, RouteOptionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        searchDisposable = mSearchPresenter.initSearchView()
        (map_fragment as SupportMapFragment).getMapAsync(this)
    }

    override fun onStop() {
        super.onStop()
        if (!searchDisposable.isDisposed) {
            searchDisposable.dispose()
        }
    }
}

