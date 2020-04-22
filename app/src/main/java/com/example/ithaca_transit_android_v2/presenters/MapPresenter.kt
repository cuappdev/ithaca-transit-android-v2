package com.example.ithaca_transit_android_v2.presenters

import android.graphics.Color
import android.util.Log
import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.DirectionType
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.states.MapLaunchState
import com.example.ithaca_transit_android_v2.states.MapState
import com.example.ithaca_transit_android_v2.states.SelectedTrip
import com.example.ithaca_transit_android_v2.states.TripOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*


class MapPresenter() {

    /**
     * Create search observable object and emit states corresponding to changes in the search bar
     */
    private fun createMapObservable(): Observable<MapState> {
        val obs = Observable.create { emitter: ObservableEmitter<MapState> ->
            val callback = fun (displayRoute: Route) {
                Log.i("qwerty", "hwwwlo")
                emitter.onNext(
                    SelectedTrip(displayRoute)
                )
            }
            Repository._updateMapView = callback

        }
        return obs.startWith(MapLaunchState())
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
                options.width(20f)
                val pattern: List<PatternItem> =
                    Arrays.asList(Dot(), Gap(15.0F))
                options.pattern(pattern)
                for (coordinate in direction.listOfCoordinates){
                    val latLng = LatLng(coordinate.latitude, coordinate.longitude)
                    options.add(latLng)

                }
            }
            map.addPolyline(options)
        }

        val lastCoor = route.directions.get(route.directions.size-1).endCoords
        var circle = CircleOptions()
        circle.center(LatLng(lastCoor.latitude, lastCoor.longitude))
        circle.fillColor(Color.rgb(0,173,255))
        circle.strokeWidth(0.0F)
        circle.radius(20.0)
        map.addCircle(circle)

        var circle1 = CircleOptions()
        circle1.center(LatLng(lastCoor.latitude, lastCoor.longitude))
        circle1.fillColor(Color.rgb(255,255,255))
        circle1.strokeWidth(0.0F)
        circle1.radius(15.0)
        map.addCircle(circle1)

        var circle2 = CircleOptions()
        circle2.center(LatLng(lastCoor.latitude, lastCoor.longitude))
        circle2.fillColor(Color.rgb(0,173,255))
        circle2.strokeWidth(0.0F)
        circle2.radius(10.0)
        map.addCircle(circle2)

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
                    is TripOptions -> {
                        drawRoute(map, state.walkingRoute)
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }
}