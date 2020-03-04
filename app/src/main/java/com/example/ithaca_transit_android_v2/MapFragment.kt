package com.example.ithaca_transit_android_v2

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ithaca_transit_android_v2.R.id.map_fragment
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Direction
import com.example.ithaca_transit_android_v2.models.DirectionType
import com.example.ithaca_transit_android_v2.models.Stop
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import java.util.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions






class MapFragment: Fragment() , OnMapReadyCallback, GoogleMap.OnPolylineClickListener{
    override fun onPolylineClick(p0: Polyline?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"





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

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("map12", "why isnt the map drawing things")
        map = googleMap
        map.addMarker(
            MarkerOptions()
                .position(LatLng(10.0, 10.0))
                .title("Hello world")
        )
        map.uiSettings.isMyLocationButtonEnabled = false
        val coordinates = listOf(Coordinate(42.442144, -76.485274), Coordinate(42.445, -76.482885))
        val startTime = Date()
        val endTime = Date()
        val startCoor = Coordinate(42.442144, -76.485274)
        val endCoor = Coordinate(42.445, -76.482885)
        val busStops = listOf(Stop("testing",42.442144, -76.485274 ))
        val busNumber = 2
        val direction = Direction(DirectionType.BUS, coordinates, startTime, endTime, startCoor, endCoor, busStops, busNumber)
        map.isMyLocationEnabled = true
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(startCoor.latitude, startCoor.longitude),14.0f))

        val sydney = LatLng(-34.0, 151.0)
        map!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        //make lat and longs
        for (coordinate in direction.listOfCoordinates) {
            val latLng = LatLng(coordinate.latitude, coordinate.longitude)
            options.add(latLng)
        }

        map.addPolyline(options)

        //mapView.onResume()
    }

}