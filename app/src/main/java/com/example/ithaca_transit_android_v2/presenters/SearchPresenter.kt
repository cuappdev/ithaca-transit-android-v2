package com.example.ithaca_transit_android_v2.presenters

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.ithaca_transit_android_v2.MapFragment
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

class SearchPresenter(
    _view: View,
    _mapFragment: MapFragment,
    _activity: Activity,
    _searchAdapter: SearchViewAdapter
) {
    var view: View = _view
    var mapFragment: MapFragment = _mapFragment
    private var mSearchLocations: MutableList<Location> = ArrayList()
    var mSearchAdapter: SearchViewAdapter = _searchAdapter

    // Needed for closing the keyboard
    private var mMainActivity: Activity = _activity

    // Represents in the ChangeLocation screen whether the start or end location was being edited
    private var mEditingStart = true

    // Represents whether the user is viewing the editing state. Used to prevent the
    // onTextChanged watcher from firing when the user first navigates to the screen
    private var mEditing = false;

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
            mapFragment.getMapView().getMapAsync { map ->
                map.setOnMapClickListener {
                    if (Repository.destinationLocation == null) {
                        emitter.onNext(SearchLaunchState())
                    } else if (Repository.startLocation != null) {
                        Repository._updateRouteOptions(false)
                        emitter.onNext(
                            RouteDisplayState(
                                Repository.startLocation!!,
                                Repository.destinationLocation!!
                            )
                        )

                    }
                    mMainActivity.hideKeyboard()
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
            view.locations_list.setOnItemClickListener { parent, view, position, id ->

                val destination = parent!!.getItemAtPosition(position) as Location
                // If the user's current location doesn't exist, default to start at startLocation.
                // Should probably change this
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
                Repository._updateRouteOptions(false)
                emitter.onNext(RouteDisplayState(startLocation, destination))
                mMainActivity.hideKeyboard()
            }

            // Move to editing the route start/end location
            view.display_route.setOnClickListener { _ ->
                val startLoc = Repository.startLocation
                val destLoc = Repository.destinationLocation
                if (startLoc != null && destLoc != null) {
                    view.edit_start_loc.setText(startLoc.name)
                    view.edit_dest_loc.setText(destLoc.name)

                    // hide the draggable routeOptions when editing location
                    Repository._updateRouteOptions(true)
                    emitter.onNext(ChangeRouteState("", true))
                }
            }

            val watcherChangeLoc: TextWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (mEditing) {
                        Log.i("qwerty", "called from editing")
                        emitter.onNext(ChangeRouteState(searchText.toString(), false))
                    }
                }
            }

            view.edit_start_loc.addTextChangedListener(watcherChangeLoc)
            view.edit_dest_loc.addTextChangedListener(watcherChangeLoc)

            view.edit_start_loc.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && mEditing) {
                    mEditingStart = true
                    emitter.onNext(ChangeRouteState(view.edit_start_loc.text.toString(), false))
                }
            }
            view.edit_dest_loc.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && mEditing) {
                    Log.i("qwerty", "called from end focus change")
                    mEditingStart = false
                    emitter.onNext(ChangeRouteState(view.edit_dest_loc.text.toString(), false))
                }
            }

            // Clicked on an updated location for the Route
            view.change_locations_list.setOnItemClickListener { parent, _, position, id ->
                if (mEditing) {
                    mEditing = false;
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
                    if (Repository.startLocation != null && Repository.destinationLocation != null) {
                        Repository._updateRouteOptions(false)
                        emitter.onNext(
                            RouteDisplayState(
                                Repository.startLocation!!,
                                Repository.destinationLocation!!
                            )
                        )
                    }
                    mMainActivity.hideKeyboard()
                }
            }

            // Switch button on the RHS was pressed
            view.switch_locations.setOnClickListener { _ ->
                val location1 = Repository.startLocation
                val location2 = Repository.destinationLocation
                Repository.startLocation = location2
                Repository.destinationLocation = location1
                if (location1 != null && location2 != null) {
                    view.edit_start_loc.setText(location2.name)
                    view.edit_dest_loc.setText(location1.name)
                    emitter.onNext(RouteDisplayState(location2, location1))
                    Repository._updateRouteOptions(true)
                }
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

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(mMainActivity))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
                        NetworkUtils().getSearchedLocations(state.searchText).toMutableList()
                    InitLocationsSearchState(state.searchText, mSearchLocations)
                } else if (state is ChangeRouteState) {
                    var offerCurrentLocationOption = false
                    if (mEditingStart) {
                        offerCurrentLocationOption = true
                    }
                    mSearchLocations =
                        NetworkUtils().getSearchedLocations(state.searchText).toMutableList()
                    ChangeRouteLocationState(
                        state.searchText,
                        mSearchLocations,
                        state.hideSearchList,
                        offerCurrentLocationOption
                    )
                } else {
                    state
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ state ->
                Log.i("qwerty", state.toString())
                when (state) {
                    is SearchLaunchState -> {
                        view.search_area.visibility = View.VISIBLE
                        view.search_empty_state.visibility = View.GONE
                        view.search_locations_state.visibility = View.GONE
                        view.change_locations_list.visibility = View.GONE
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
                            mSearchAdapter.swapItems(state.searchedLocations, false)
                        }
                    }
                    is RouteDisplayState -> {
                        view.search_area.visibility = View.GONE
                        view.search_locations_state.visibility = View.GONE
                        view.change_locations_list.visibility = View.GONE

                        view.search_change_location_holder.visibility = View.VISIBLE
                        view.edit_route.visibility = View.GONE
                        view.display_route.visibility = View.VISIBLE

                        view.display_start_loc.text = state.startLocation.name
                        view.display_dest_loc.text = state.endLocation.name
                    }
                    is ChangeRouteLocationState -> {
                        view.display_route.visibility = View.GONE
                        if (state.hideSearchList) {
                            view.change_locations_list.visibility = View.GONE
                        } else {
                            view.change_locations_list.visibility = View.VISIBLE
                        }
                        if (state.searchedLocations != null && state.searchedLocations.isNotEmpty()) {
                            mSearchAdapter.swapItems(
                                state.searchedLocations,
                                state.offerCurrentLocationOption
                            )
                        } else if ("Current Location".contains(
                                state.searchText,
                                ignoreCase = true
                            )
                        ) {
                            mSearchAdapter.swapItems(ArrayList(), state.offerCurrentLocationOption)
                        }
                        view.edit_route.visibility = View.VISIBLE
                        mEditing = true;
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }
}