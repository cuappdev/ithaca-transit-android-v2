package com.example.ithaca_transit_android_v2.presenters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.ithaca_transit_android_v2.NetworkUtils

import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.LocationType

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




class MapPresenter() {


    /**
     * Create search observable object and emit states corresponding to changes in the search bar
     */
    private fun createMapObservable(): Observable<MapState> {
        val obs = Observable.create { emitter: ObservableEmitter<MapState> ->
            emitter.onNext(MapLaunchState())
        }
        return obs.startWith(MapLaunchState())
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
                                    42.444,
                                    76.5019
                                ), 6.0f
                            )
                        )
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }
}