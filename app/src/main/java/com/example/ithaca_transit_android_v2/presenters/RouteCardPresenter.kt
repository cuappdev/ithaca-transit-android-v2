package com.example.ithaca_transit_android_v2.presenters

import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.RouteOptions
import com.example.ithaca_transit_android_v2.states.RouteCardState
import com.example.ithaca_transit_android_v2.states.RouteDetailViewState
import com.example.ithaca_transit_android_v2.states.RouteHiddenState
import com.example.ithaca_transit_android_v2.states.RouteOptionState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.route_card_compact.view.*

class RouteCardPresenter(bottomSheet: View) {

    var routeCardHolder: View = bottomSheet

    private fun createRouteCardObservable(): Observable<RouteCardState> {
        val obs = Observable.create { emitter: ObservableEmitter<RouteCardState> ->

            Repository.destinationListListeners!!.addOnItemClickListener(
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    emitter.onNext(
                        RouteOptionState(
                            RouteOptions(
                                ArrayList(),
                                ArrayList(),
                                ArrayList()
                            )
                        )
                    )
                })
        }
        return obs.startWith(RouteHiddenState())
    }

    fun initRouteCardView(): Disposable {
        val observable = createRouteCardObservable()


        return observable
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is RouteHiddenState -> {
                        routeCardHolder.visibility = View.GONE
                    }
                    is RouteOptionState -> {
                        routeCardHolder.visibility = View.VISIBLE
                    }
                    is RouteDetailViewState -> {
                        routeCardHolder.nearby_stops_routes.visibility = View.GONE

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