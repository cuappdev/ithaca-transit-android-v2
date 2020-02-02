package com.example.ithaca_transit_android_v2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private lateinit var mSearchAdapter: SearchViewAdapter
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
        mSearchAdapter = SearchViewAdapter(this, mSearchLocations)
        mSearchPresenter = SearchPresenter(search_view, this, mSearchAdapter)

        searchDisposable = mSearchPresenter.initSearchView()
        (map_fragment as SupportMapFragment).getMapAsync(this)

        // set up search adapter
        locations_list.adapter = mSearchAdapter
        locations_list.setOnItemClickListener { parent, view, position, id ->
            val destination = parent.getItemAtPosition(position) as Location
            // TODO: change map state to reflect destination

            // for debugging purposes only
            Toast.makeText(this, destination.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!searchDisposable.isDisposed) {
            searchDisposable.dispose()
        }
    }
}

