package com.example.ithaca_transit_android_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ithaca_transit_android_v2.models.Location
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = OkHttpClient()
        val url = "https://transit-backend.cornellappdev.com/api/v2/"

        val network = NetworkUtils()
        network.getSearchedLocations(client,url,"7-Eleven")

    }
}
