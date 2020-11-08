package com.example.ithaca_transit_android_v2.presenters

import android.graphics.Color
import android.util.Log
import com.example.ithaca_transit_android_v2.NetworkUtils

import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.*
import com.example.ithaca_transit_android_v2.models.tracking.BusInformation

import com.example.ithaca_transit_android_v2.states.*
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter
import com.google.android.gms.maps.GoogleMap
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.search_main.view.*
import kotlinx.android.synthetic.main.search_secondary.view.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapPresenter() {

    private val polylines = mutableListOf<Polyline>()
    /**
     * Create search observable object and emit states corresponding to changes in the search bar
     */
    private fun createMapObservable(): Observable<MapState> {
        val obs = Observable.create { emitter: ObservableEmitter<MapState> ->
            val callback = fun (displayRoute: Route) {

                for(polyline in polylines) {
                    polyline.remove()
                }
                polylines.clear()

                emitter.onNext(
                    SelectedTrip(displayRoute)
                )
                liveTrackingTEST(displayRoute)
            }
            Repository._updateMapView = callback


            val clearCallback = fun () {

                for(polyline in polylines) {
                    polyline.remove()
                }
                polylines.clear()
            }
            Repository._clearMapView = clearCallback


        }
        return obs.startWith(MapLaunchState())
    }

    fun liveTrackingTEST(route: Route) {
        Thread(Runnable {
            for(direction in route.directions) {
                if (direction.tripIds.isNotEmpty()) {
                    val busInfo = BusInformation(
                        direction.tripIds.first(),
                        direction.routeId
                    )
                    NetworkUtils().getBusCoords(listOf(busInfo))
                }
            }
        }).start()
    }

    fun drawRoute(map: GoogleMap, route: Route) {
        for(direction in route.directions){
            val options = PolylineOptions()
            if (direction.type == DirectionType.BUS) {
                options.color(Color.rgb(0,173,255))
                options.width(10f)
                for (coordinate in direction.listOfCoordinates){
                    val latLng = LatLng(coordinate.latitude, coordinate.longitude)
                    options.add(latLng)
                }
            }
            if(direction.type == DirectionType.WALK) {
                options.color(Color.rgb(160,160,160))
                options.width(10f)
                for (coordinate in direction.listOfCoordinates){
                    val latLng = LatLng(coordinate.latitude, coordinate.longitude)
                    options.add(latLng)
                }
            }
            val polyline =  map.addPolyline(options)
            polylines.add(polyline)
        }
    }

    /**
     * Ran during onCreate() of the main activity to subscribe onto the state changes for search
     */
    fun initMapView(map: GoogleMap): Disposable {
        val observable = createMapObservable()

        return observable
            .observeOn(Schedulers.io())
            .map { state -> state
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ state ->
                when(state) {
                    is MapLaunchState -> {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    42.4491,
                                    -76.4833
                                ), 15.5f
                            )
                        )
                    }
                    is SelectedTrip -> {
                        drawRoute(map,state.selectedRoute)
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }
}