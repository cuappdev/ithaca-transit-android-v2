package com.example.ithaca_transit_android_v2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.states.*
import com.example.ithaca_transit_android_v2.ui_adapters.SearchViewAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toolbar_search.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var disposable: Disposable
    private var mSearchLocations: List<Location> = ArrayList()
    private var mSearchAdapter: SearchViewAdapter? = null

    override fun onMapReady(map: GoogleMap?) {
        map!!.setOnMapClickListener { point ->
            Log.i("qwerty", "map clicked")
            search_view.clearFocus()
        }
    }

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

            search_input.addTextChangedListener(watcher)
            search_input.setOnFocusChangeListener { view, hasFocus ->
                Log.i("qwerty", "focus changed "+hasFocus)
                if (hasFocus && (view as EditText).text.isEmpty()) {
                    // search input clicked on with no text
                    emitter.onNext(EmptyInitClickState())
                } else if (hasFocus) {
                    // search input clicked on with text query
                    emitter.onNext(InitSearchState((view as EditText).text.toString()))
                } else {
                    Log.i("qwerty", "cleared")
                    // search input not focused - display the un-expanded version
                    emitter.onNext(SearchLaunchState())
                }
            }

            emitter.setCancellable {
                search_input.removeTextChangedListener(watcher)
                search_input.setOnFocusChangeListener(null)
            }
        }
        return obs.startWith(SearchLaunchState())
    }

    private fun initSearchView() {
        val observable = createSearchObservable()

        disposable = observable
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
                when (state) {
                    is SearchLaunchState -> {
                        Log.i("qwerty", "In Search Launch State")
                        search_empty_state.visibility = View.GONE
                        search_locations_state.visibility = View.GONE
                    }
                    is EmptyInitClickState -> {
                        search_locations_state.visibility = View.GONE
                        search_empty_state.visibility = View.VISIBLE
                    }
                    is InitLocationsSearchState -> {
                        search_empty_state.visibility = View.GONE
                        search_locations_state.visibility = View.VISIBLE
                        if (state.searchedLocations!!.size > 0 || search_input.text.isEmpty()) {
                            mSearchAdapter!!.swapItems(state.searchedLocations)
                        }
                    }
                }
            }, { error -> Log.e("An Error Occurred", error.toString()) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSearchAdapter = SearchViewAdapter(this, mSearchLocations)
        locations_list.adapter = mSearchAdapter
        locations_list.setOnItemClickListener { parent, view, position, id ->
            val destination = parent.getItemAtPosition(position) as Location

            val intent = Intent(this, RouteOptionsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


        }
        initSearchView()
        (map_fragment as SupportMapFragment).getMapAsync(this)
    }

    override fun onStop() {
        super.onStop()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}

