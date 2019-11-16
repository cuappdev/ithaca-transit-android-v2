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
                NetworkUtils().getRouteOptions("{\n" +
                        "  \"end\" : \"42.444971674516864,-76.48098092526197\",\n" +
                        "  \"uid\" : \"E4A0256E-5865-4E9F-8A5A-33747CAC7EBF\",\n" +
                        "  \"time\" : 1573515642.318114,\n" +
                        "  \"destinationName\" : \"Bill & Melinda Gates Hall\",\n" +
                        "  \"start\" : \"42.44717985041025,-76.48551732274225\",\n" +
                        "  \"arriveBy\" : false,\n" +
                        "  \"originName\" : \"Current Location\"\n" +
                        "}")
            }.await()

            Log.d("testing-final", deferred.toString())
        }
        //
    }
}