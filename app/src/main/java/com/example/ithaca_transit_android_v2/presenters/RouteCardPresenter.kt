package com.example.ithaca_transit_android_v2.presenters

import android.content.Context
import android.content.Intent
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import com.example.ithaca_transit_android_v2.states.RouteOptionState
import com.example.ithaca_transit_android_v2.views.RouteViewInterface
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RouteCardPresenter(viewInt: RouteViewInterface){

    fun determineState(routeObservable: Observable<RouteCardState>): RouteCardState{

        var stateR : RouteCardState
        val disposable = routeObservable
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map{state ->
                when (state) {
                    is RouteDetailViewState -> {

                    }

                    }


            }
    }

}