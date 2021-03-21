package com.cornellappdev.android.ithaca_transit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cornellappdev.android.ithaca_transit.presenters.MapPresenter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.reactivex.disposables.Disposable

class MapFragment: Fragment() , OnMapReadyCallback, GoogleMap.OnPolylineClickListener{
    override fun onPolylineClick(p0: Polyline?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mapView: MapView
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private lateinit var mMapPresenter: MapPresenter
    private lateinit var mapDisposable: Disposable


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var v = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = v.findViewById(R.id.mapView2)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mapView.onCreate(mapViewBundle)
        mMapPresenter = MapPresenter()

        //mapView.getMapAsync() { this }
        mapView.getMapAsync(this)
        return v
    }
    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
         mapView.onSaveInstanceState(mapViewBundle)

    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    fun getMapView():MapView {
        return mapView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapDisposable = mMapPresenter.initMapView(googleMap)
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        if (Repository.isPermissionGranted) {
            googleMap.isMyLocationEnabled = true
        }
    }
}