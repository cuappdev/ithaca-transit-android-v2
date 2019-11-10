package com.example.ithaca_transit_android_v2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ithaca_transit_android_v2.networking.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO (lesley): just for testing purposes - replace with rxjava
        runBlocking {
            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getSearchedLocations("7-Eleven")
            }.await()

            Log.d("testing-final", deferred.toString())
        }
        //
    }
}