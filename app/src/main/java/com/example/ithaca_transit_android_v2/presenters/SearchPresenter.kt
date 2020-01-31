package com.example.ithaca_transit_android_v2.presenters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.ithaca_transit_android_v2.NetworkUtils
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.states.*
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_toolbar_search.view.*

class SearchPresenter(_view: View, _context: Context) {
    var view: View = _view
    var context: Context = _context
    private var mSearchLocations: List<Location> = ArrayList()
    var mSearchAdapter: SearchViewAdapter = SearchViewAdapter(context, mSearchLocations)

    fun createSearchObservable(): Observable<SearchState> {
        val obs = Observable.create { emitter: ObservableEmitter<SearchState> ->
            val watcher: TextWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.d("search presenter", "on text changed")
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
                    Log.d("search presenter", "empty init click state")
                    emitter.onNext(EmptyInitClickState())
                } else if (hasFocus) {
                    // search input clicked on with text query
                    Log.d("search presenter", "init search state")
                    emitter.onNext(InitSearchState((view as EditText).text.toString()))
                } else {
                    // search input not focused - display the un-expanded version
                    Log.d("search presenter", "search launch state")
                    emitter.onNext(SearchLaunchState())
                }
            }

            emitter.setCancellable {
                view.search_input.removeTextChangedListener(watcher)
                view.search_input.setOnFocusChangeListener(null)
            }
        }
        return obs.startWith(SearchLaunchState())
    }

    fun initSearchView(): Disposable {
        val observable = createSearchObservable()

        mSearchAdapter = SearchViewAdapter(context, mSearchLocations)

        return observable
            .observeOn(Schedulers.io())
            .map { state ->
                if (state is InitSearchState) {
                    val locations = NetworkUtils().getSearchedLocations(state.searchText)
                    InitLocationsSearchState(state.searchText, locations)
                } else {
                    state
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ state ->
                Log.d("search presenter", state.toString())
                when (state) {
                    is SearchLaunchState -> {
                        view.search_empty_state.visibility = View.GONE
                        view.search_locations_state.visibility = View.GONE
                    }
                    is EmptyInitClickState -> {
                        view.search_locations_state.visibility = View.GONE
                        view.search_empty_state.visibility = View.VISIBLE
                    }
                    is InitLocationsSearchState -> {
                        view.search_empty_state.visibility = View.GONE
                        view.search_locations_state.visibility = View.VISIBLE
                        if (state.searchedLocations!!.isNotEmpty() || view.search_input.text.isEmpty()) {
                            mSearchAdapter.swapItems(state.searchedLocations)
                        }
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }
}