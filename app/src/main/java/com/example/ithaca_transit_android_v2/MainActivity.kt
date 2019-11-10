package com.example.ithaca_transit_android_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ithaca_transit_android_v2.networking.NetworkUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val test = GlobalScope.async { NetworkUtils().getSearchedLocations("balch")}.getCompleted()
        Log.d("testing", test.toString())
    }
}