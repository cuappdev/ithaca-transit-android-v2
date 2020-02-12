package com.example.ithaca_transit_android_v2.presenters

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.ithaca_transit_android_v2.models.Route
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import com.example.ithaca_transit_android_v2.states.RouteOptionState

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.route_card_compact.view.*

class RouteCardPresenter(_view: View, _context: Context) {

    var view: View = _view

    fun initRouteCardView(routeObservable: Observable<RouteCardState>): Disposable {

        return routeObservable
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is RouteDetailViewState -> {
                        view.nearby_stops_routes.visibility = View.GONE

                    }

                }

            }
    }

}