package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView.OnLeftMenuClickListener
import com.arlib.floatingsearchview.FloatingSearchView.OnQueryChangeListener
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.example.ithaca_transit_android_v2.LocationAutocomplete
import com.example.ithaca_transit_android_v2.NetworkUtils
import com.example.ithaca_transit_android_v2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class SearchViewDelegate @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {
    val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflator.inflate(R.layout.search_view, this, true)
    val searchView = findViewById<FloatingSearchView>(R.id.search_view)

    fun render() {
        // Set up the menu icon for displaying left drawer (currently unimplemented)
        searchView.setOnLeftMenuClickListener(object : OnLeftMenuClickListener {
            override fun onMenuOpened() {
                Toast.makeText(context, "onMenuOpened", Toast.LENGTH_SHORT).show()
            }

            override fun onMenuClosed() {
                Toast.makeText(context, "onMenuClosed", Toast.LENGTH_SHORT).show()
            }
        })

        searchView.setOnQueryChangeListener(OnQueryChangeListener { oldQuery, newQuery ->
            // get suggestions based on newQuery, pass them on to the search view
            runBlocking {
                val results = CoroutineScope(Dispatchers.IO).async {
                    NetworkUtils().getSearchedLocations(newQuery)
                }.await()
//                Log.d("SearchViewDelegate", results.toString())
                var wrappedResults = mutableListOf<LocationAutocomplete>()
                if (results != null) {
                    for (loc in results) {
                        wrappedResults.add(LocationAutocomplete(loc))
                    }
                }
                searchView.swapSuggestions(wrappedResults)
            }
//            Toast.makeText(context, newQuery, Toast.LENGTH_SHORT).show()
        })
    }
}