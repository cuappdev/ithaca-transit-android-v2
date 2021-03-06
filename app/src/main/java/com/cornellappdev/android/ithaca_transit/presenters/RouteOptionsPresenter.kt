package com.cornellappdev.android.ithaca_transit.presenters

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.cornellappdev.android.ithaca_transit.NetworkUtils
import com.cornellappdev.android.ithaca_transit.Repository
import com.cornellappdev.android.ithaca_transit.models.Route
import com.cornellappdev.android.ithaca_transit.states.OptionsHiddenState
import com.cornellappdev.android.ithaca_transit.states.RouteCardState
import com.cornellappdev.android.ithaca_transit.states.RouteDetailViewState
import com.cornellappdev.android.ithaca_transit.states.RouteListState
import com.cornellappdev.android.ithaca_transit.ui_adapters.RouteDetailAdapter
import com.cornellappdev.android.ithaca_transit.ui_adapters.RouteListAdapterObject
import com.cornellappdev.android.ithaca_transit.ui_adapters.RouteListViewAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.routes_holder.view.*
import kotlinx.android.synthetic.main.route_detailed_holder.view.*

class RouteOptionsPresenter(
    private val bottom_sheet: View,
    _routeListViewAdapter: RouteListViewAdapter,
    _routeDetailAdapter: RouteDetailAdapter,
    _context: Context
) {

    var mRouteListAdapter = _routeListViewAdapter
    var mRouteDetailAdapter = _routeDetailAdapter
    var routeCardHolder: View = bottom_sheet.bottom_sheet_data
    private val context = _context
    private var mSlidingDisabled = true

    private fun createRouteCardObservable(): Observable<RouteCardState> {
        val obs = Observable.create { emitter: ObservableEmitter<RouteCardState> ->

            // This callback is what is used to control RouteOptions from the SearchView
            val searchCallback = fun(hidden: Boolean) {
                if (hidden) {
                    emitter.onNext(OptionsHiddenState())
                    mSlidingDisabled = true
                } else if (Repository.startLocation != null && Repository.destinationLocation != null) {
                    emitter.onNext(
                        RouteListState(
                            Repository.startLocation!!,
                            Repository.destinationLocation!!, null
                        )
                    )
                    mSlidingDisabled = false
                }
            }
            Repository._updateRouteFromSearch = searchCallback

            val adapterCallback = fun(route: Route) {
                Repository._updateMapView(route)
                emitter.onNext(RouteDetailViewState(route))
            }
            Repository._updateRouteDetailed = adapterCallback

            bottom_sheet.back.setOnClickListener { view ->
                if (Repository.startLocation != null && Repository.destinationLocation != null) {
                    emitter.onNext(
                        RouteListState(
                            Repository.startLocation!!,
                            Repository.destinationLocation!!, null
                        )
                    )
                }
            }
        }
        return obs.startWith(OptionsHiddenState())
    }

    fun setBottomSheetHeight(height: Float) {
        BottomSheetBehavior.from(bottom_sheet).peekHeight =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                height, context.resources.displayMetrics
            ).toInt()

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
                    if(routeOptions.boardingSoon != null && routeOptions.boardingSoon.isNotEmpty()) {
                        NetworkUtils().applyDelayRoutes(routeOptions.boardingSoon)
                    }
                    if(routeOptions.fromStop != null && routeOptions.fromStop.isNotEmpty()) {
                        NetworkUtils().applyDelayRoutes(routeOptions.fromStop)
                    }
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
                        bottom_sheet.route_detailed_holder.visibility = View.GONE
                        bottom_sheet.back.hide()
                        setBottomSheetHeight(65f)
                    }
                    is RouteListState -> {
                        // Display the first routeOptions route on the map
                        if (state.routeOptions?.boardingSoon == null
                            || state.routeOptions.fromStop == null
                            || state.routeOptions.walking == null) {
                            Repository._clearMapView();
                            return@subscribe
                        }

                        /**
                         * When a route is clicked on in the ListView, and someone stays on it in
                         * the RouteDetailHolder, if they go back the Route is still there, unsure whether
                         * or not to filter out old routes or re-update the list
                         */
//                        //Filter out old routes?
//                        state.routeOptions.boardingSoon.filter { route ->
//                            Route.computeBoardInMinWithDelay(route) > 0
//                        }
//
//                        state.routeOptions.fromStop.filter { route ->
//                            Route.computeBoardInMinWithDelay(route) > 0
//                        }
//
                        //update?
//                        state.routeOptions = NetworkUtils().getRouteOptions(
//                            state.startLocation.coordinate,
//                            state.destLocation.coordinate,
//
//                            System.currentTimeMillis() / 1000.0,
//                            false,
//                            state.destLocation.name
//                        )


                        if (state.routeOptions.boardingSoon.isNotEmpty()) {
                            Repository._updateMapView(state.routeOptions.boardingSoon[0])
                        } else if (state.routeOptions.fromStop.isNotEmpty()) {
                            Repository._updateMapView(state.routeOptions.fromStop[0])
                        } else if (state.routeOptions.walking.isNotEmpty()) {
                            Repository._updateMapView(state.routeOptions.walking[0])
                        }

                        setBottomSheetHeight(400f)

                        // Refresh Views
                        routeCardHolder.boarding_soon_routes.invalidate()
                        routeCardHolder.boarding_soon_routes.removeAllViews()

                        val allRoutes: ArrayList<RouteListAdapterObject> =
                            ArrayList<RouteListAdapterObject>()
                        if (state.routeOptions.boardingSoon.isNotEmpty()) {
                            allRoutes.add(
                                RouteListAdapterObject(
                                    "routeLabel",
                                    "Boarding Soon from Nearby Stops"
                                )
                            )

                            //Add all route objects and texts
                            for (r in state.routeOptions.boardingSoon) {
                                val boardObj: RouteListAdapterObject =
                                    RouteListAdapterObject("route", r)
                                allRoutes.add(boardObj)
                            }
                        }

                        if (state.routeOptions.fromStop.isNotEmpty()) {
                            allRoutes.add(RouteListAdapterObject("routeLabel", "From Stops"))

                            //Add all route objects and texts
                            for (r in state.routeOptions.fromStop) {
                                val boardObj: RouteListAdapterObject =
                                    RouteListAdapterObject("route", r)
                                allRoutes.add(boardObj)
                            }
                        }

                        if (state.routeOptions.walking.isNotEmpty()) {
                            allRoutes.add(RouteListAdapterObject("routeLabel", "Walking"))

                            //Add all route objects and texts
                            for (r in state.routeOptions.walking) {
                                val boardObj: RouteListAdapterObject =
                                    RouteListAdapterObject("route", r)
                                allRoutes.add(boardObj)
                            }
                        }

                        mRouteListAdapter.swapItems(allRoutes)

                        val count = routeCardHolder.boarding_soon_routes.childCount
                        for (i in 1..count - 1) {
                            routeCardHolder.boarding_soon_routes.getChildAt(i).invalidate()

                        }

                        routeCardHolder.visibility = View.VISIBLE
                        bottom_sheet.route_detailed_holder.visibility = View.GONE
                        bottom_sheet.back.hide()
                    }
                    is RouteDetailViewState -> {
                        routeCardHolder.visibility = View.GONE
                        mRouteDetailAdapter.updateRouteDetail(state.route)
                        bottom_sheet.route_detailed_holder.visibility = View.VISIBLE
                        bottom_sheet.back.show()

                    }

                }

            }
    }

    fun setBottomSheetCallback(
        bottom_sheet: LinearLayout
    ) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
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
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }

            }

            override fun onStateChanged(@NonNull view: View, i: Int) {
                if (mSlidingDisabled) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

        })
    }

}