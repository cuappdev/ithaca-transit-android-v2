package com.example.ithaca_transit_android_v2.presenters

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.example.ithaca_transit_android_v2.NetworkUtils
import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.states.OptionsHiddenState
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import com.example.ithaca_transit_android_v2.states.RouteListState
import com.example.ithaca_transit_android_v2.ui_adapters.RouteViewAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.route_card_compact.view.*

class RouteOptionsPresenter(bottomSheet: View, _routeViewAdapter: RouteViewAdapter) {

    var mRoutesAdapter = _routeViewAdapter
    var routeCardHolder: View = bottomSheet

    private fun createRouteCardObservable(): Observable<RouteCardState> {
        val obs = Observable.create { emitter: ObservableEmitter<RouteCardState> ->
            val callback = fun(hidden: Boolean) {
                if (hidden) {
                    emitter.onNext(OptionsHiddenState())
                } else if (Repository.startLocation != null && Repository.destinationLocation != null) {

                    emitter.onNext(
                        RouteListState(
                            Repository.startLocation!!,
                            Repository.destinationLocation!!, null
                        )
                    )
                }
            }
            Repository._updateRouteOptions = callback

        }
        return obs.startWith(OptionsHiddenState())
    }

    fun initRouteCardView(): Disposable {
        val observable = createRouteCardObservable()

        return observable
            .observeOn(Schedulers.io())
            .map { state ->
                if (state is RouteListState) {
                    val routeOptions = NetworkUtils().getRouteOptions(
                        state.startLocation.coordinate,
                        state.destLocation.coordinate,

                        System.currentTimeMillis() / 1000.0,
                        false,
                        state.destLocation.name
                    )
                    RouteListState(state.startLocation, state.destLocation, routeOptions)
                } else {
                    state
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is OptionsHiddenState -> {
                        routeCardHolder.visibility = View.GONE
                    }
                    is RouteListState -> {
                        // Display the first routeOptions route on the map
                        Repository._updateMapView(state.routeOptions!!.boardingSoon[0])

                        // Refresh Views
                        routeCardHolder.boarding_soon_routes.invalidate()
                        routeCardHolder.boarding_soon_routes.removeAllViews()

                        mRoutesAdapter.swapItems(ArrayList(state.routeOptions!!.boardingSoon))

                        val count = routeCardHolder.boarding_soon_routes.childCount
                        for (i in 1..count - 1) {
                            routeCardHolder.boarding_soon_routes.getChildAt(i).invalidate()

                        }

                        routeCardHolder.from_stop_routes.invalidate()
                        routeCardHolder.from_stop_routes.removeAllViews()

                        mRoutesAdapter.swapItems(ArrayList(state.routeOptions!!.boardingSoon))





                        routeCardHolder.visibility = View.VISIBLE
                    }
                    is RouteDetailViewState -> {
                        routeCardHolder.boarding_soon_routes.visibility = View.GONE

                    }

                }

            }
    }

    fun setBottomSheetCallback(
        bottomSheetBehavior: BottomSheetBehavior<LinearLayout>,
        bottom_sheet: LinearLayout
    ) {
        var slideState = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

                val location = IntArray(2)
                bottom_sheet.getLocationOnScreen(location)

                /*
                * Tells the slider to snap to the middle when the user lifts their finger near the middle of the screen.
                * */
                if (location[1] > 600 && location[1] < 1000) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
                }

            }

            override fun onStateChanged(@NonNull view: View, i: Int) {
                when (i) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        slideState = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        slideState = BottomSheetBehavior.STATE_EXPANDED
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> slideState =
                        BottomSheetBehavior.STATE_HALF_EXPANDED
                    BottomSheetBehavior.STATE_DRAGGING,
                    BottomSheetBehavior.STATE_HIDDEN,
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

        })
    }

}