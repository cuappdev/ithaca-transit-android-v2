package com.example.ithaca_transit_android_v2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.networking.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val end = Coordinate(42.444971674516864, -76.48098092526197)
        val uid = "E4A0256E-5865-4E9F-8A5A-33747CAC7EBF"
        val time = 1573515642.318114
        val destinationName = "Bill & Melinda Gates Hall"
        val start = Coordinate(42.44717985041025, -76.48551732274225)
        val arriveBy = false

        // TODO (lesley): just for testing purposes - replace with rxjava

        runBlocking {
            val deferred = CoroutineScope(Dispatchers.IO).async {
                NetworkUtils().getRouteOptions(end, uid, time, destinationName, start, arriveBy)
            }.await()

            Log.d("testing-final", deferred.toString())

        }

    }
}
