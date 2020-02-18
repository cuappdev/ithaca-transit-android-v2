package com.example.ithaca_transit_android_v2.presenters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.example.ithaca_transit_android_v2.NetworkUtils

import com.example.ithaca_transit_android_v2.Repository
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.LocationType

import com.example.ithaca_transit_android_v2.states.*
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.search_main.view.*
import kotlinx.android.synthetic.main.search_secondary.view.*


class SearchPresenter(_view: View, _context: Context, _searchAdapter: SearchViewAdapter) {
    var view: View = _view
    private var mSearchLocations: List<Location> = ArrayList()
    var mSearchAdapter: SearchViewAdapter = _searchAdapter

    // Represents in the ChangeLocation screen whether the start or end location was being edited
    private var mEditingStart = true

    /**
     * Create search observable object and emit states corresponding to changes in the search bar
     */
    private fun createSearchObservable(): Observable<SearchState> {
        val obs = Observable.create { emitter: ObservableEmitter<SearchState> ->
            val watcher: TextWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (searchText!!.isEmpty()) {
                        emitter.onNext(EmptyInitClickState())
                    } else {
                        emitter.onNext(InitSearchState(searchText.toString()))
                    }
                }
            }

            view.search_input.addTextChangedListener(watcher)
            view.search_input.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && (view as EditText).text.isEmpty()) {
                    // search input clicked on with no text
                    emitter.onNext(EmptyInitClickState())
                } else if (hasFocus) {
                    // search input clicked on with text query
                    emitter.onNext(InitSearchState((view as EditText).text.toString()))
                }
            }

            // Location clicked, default start location is CurrLocation
            Repository.destinationListListeners!!.addOnItemClickListener(
                AdapterView.OnItemClickListener{parent, view, position, id ->

                val destination = parent!!.getItemAtPosition(position) as Location
                var startLocation = destination

                // Get the location object of the user, transform it into a custom "Current Location" object
                val myLoc = Repository.currentLocation
                if (myLoc != null) {
                    startLocation = Location(
                        LocationType.APPLE_PLACE, "Current Location",
                        Coordinate(myLoc.latitude, myLoc.longitude), ""
                    )
                }
                Repository.startLocation = startLocation
                Repository.destinationLocation = destination
                emitter.onNext(RouteDisplayState(startLocation, destination))
                })
            // Move to editing the route start/end location
            view.display_route.setOnClickListener { _ ->
                val startLoc = Repository.startLocation
                val destLoc = Repository.destinationLocation
                if (startLoc != null && destLoc != null) {
                    view.edit_start_loc.setText(startLoc.name)
                    view.edit_dest_loc.setText(destLoc.name)
                    emitter.onNext(ChangeRouteState("", true))
                }
            }

            val watcherChangeLoc: TextWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    emitter.onNext(ChangeRouteState(searchText.toString(), false))
                }
            }

            view.edit_start_loc.addTextChangedListener(watcherChangeLoc)
            view.edit_dest_loc.addTextChangedListener(watcherChangeLoc)

            view.edit_start_loc.setOnFocusChangeListener {view, hasFocus ->
                if (hasFocus) {
                    mEditingStart = true
                    emitter.onNext(ChangeRouteState("", true))
                }
            }
            view.edit_dest_loc.setOnFocusChangeListener{view, hasFocus ->
                if (hasFocus) {
                    mEditingStart = false
                    emitter.onNext(ChangeRouteState("", true))
                }
            }

            view.locations_list_2.setOnItemClickListener { parent, _, position, id ->
                val location = parent.getItemAtPosition(position) as Location
                // Depending on whether they were editing the start or destination field, change how
                // things get updated
                if (mEditingStart) {
                    Repository.startLocation = location
                    view.edit_start_loc.setText(location.name)
                } else {
                    Repository.destinationLocation = location
                    view.edit_dest_loc.setText(location.name)
                }
                emitter.onNext(ChangeRouteState("", true))

            }

            emitter.setCancellable {
                view.search_input.removeTextChangedListener(watcher)
                view.edit_start_loc.removeTextChangedListener(watcherChangeLoc)
                view.search_input.setOnFocusChangeListener(null)
                view.locations_list.setOnItemClickListener(null)
                view.display_route.setOnClickListener(null)

            }
        }
        return obs.startWith(SearchLaunchState())
    }

    /**
     * Ran during onCreate() of the main activity to subscribe onto the state changes for search
     */
    fun initSearchView(): Disposable {
        val observable = createSearchObservable()

        return observable
            .observeOn(Schedulers.io())
            .map { state ->
                if (state is InitSearchState) {
                    mSearchLocations =
                        NetworkUtils().getSearchedLocations(state.searchText) ?: ArrayList()
                    InitLocationsSearchState(state.searchText, mSearchLocations)
                } else if (state is ChangeRouteState) {
                    mSearchLocations =
                        NetworkUtils().getSearchedLocations(state.searchText) ?: ArrayList()
                    ChangeRouteLocationState(state.searchText, mSearchLocations, state.hideSearchList)

                } else {
                    state
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ state ->
                when (state) {
                    is SearchLaunchState -> {
                        view.search_area.visibility = View.VISIBLE
                        view.search_empty_state.visibility = View.GONE
                        view.search_locations_state.visibility = View.GONE
                        view.locations_list_2.visibility = View.GONE

                        view.search_change_location_holder.visibility = View.GONE

                    }
                    is EmptyInitClickState -> {
                        view.search_locations_state.visibility = View.GONE
                        view.search_empty_state.visibility = View.VISIBLE
                    }
                    is InitLocationsSearchState -> {
                        view.search_empty_state.visibility = View.GONE
                        view.search_locations_state.visibility = View.VISIBLE
                        if (state.searchedLocations!!.size > 0 || view.search_input.text.isEmpty()) {
                            mSearchAdapter.swapItems(state.searchedLocations)
                        }
                    }
                    is RouteDisplayState -> {
                        view.search_area.visibility = View.GONE
                        view.search_locations_state.visibility = View.GONE

                        view.search_change_location_holder.visibility = View.VISIBLE
                        view.edit_route.visibility = View.GONE
                        view.display_route.visibility = View.VISIBLE

                        view.display_start_loc.text = state.startLocation.name
                        view.display_dest_loc.text = state.endLocation.name
                    }
                    is ChangeRouteLocationState -> {
                        view.display_route.visibility = View.GONE
                        if (state.searchedLocations != null && state.searchedLocations.size > 0) {
                            mSearchAdapter.swapItems(state.searchedLocations)
                        } else if(state.searchText === "") {
                            mSearchAdapter.swapItems(ArrayList())
                        }
                        if(state.hideSearchList) {
                            view.locations_list_2.visibility = View.GONE
                        } else {
                            view.locations_list_2.visibility = View.VISIBLE
                        }
                        view.edit_route.visibility = View.VISIBLE
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }
}