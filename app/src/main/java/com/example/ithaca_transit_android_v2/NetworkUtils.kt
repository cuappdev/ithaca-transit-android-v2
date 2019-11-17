package com.example.ithaca_transit_android_v2.networking

import android.util.Log
import com.example.ithaca_transit_android_v2.CustomDateAdapter
import com.example.ithaca_transit_android_v2.LocationAdapter
import com.example.ithaca_transit_android_v2.RouteAdapter
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.RouteOptions
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Date

/*
 * NetworkUtils include all the networking calls needed
 */
class NetworkUtils {

    val client = OkHttpClient()
    val url = "https://transit-backend.cornellappdev.com/api/v2/"
    val mediaType = ("application/json; charset=utf-8").toMediaType()

    // Function that takes in query and returns list of Locations
    fun getSearchedLocations(query: String): List<Location> {
        val json = JSONObject()
        json.put("query", query)
        val requestBody = json.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url + "search")
            .post(requestBody)
            .build()

        val body = client.newCall(request).execute().body?.string()
        val type = newParameterizedType(List::class.java, Location::class.java)
        val moshi = Moshi.Builder()
            .add(LocationAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter: JsonAdapter<List<Location>> = moshi.adapter(type)
        return adapter.fromJson(body) ?: emptyList()
    }

    fun getAllBusStops(): List<Location> {
        val request: Request = Request.Builder()
            .url(url + "allstops")
            .build()

        val body = client.newCall(request).execute().body?.string()
        val type = newParameterizedType(List::class.java, Location::class.java)
        val moshi = Moshi.Builder()
            .add(LocationAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter: JsonAdapter<List<Location>> = moshi.adapter(type)
        return adapter.fromJson(body) ?: emptyList()
    }

    fun getRouteOptions(
        end: Coordinate,
        uid: String,
        // Temporarily a string until we figure out how to pass a date object correctly
        time: Double,
        destName : String,
        start : Coordinate,
        arriveBy: Boolean,
        originName: String = "Current Location" ): RouteOptions {


        val json = JSONObject()
        json.put("end", end.toString())
        json.put("uid", uid)
        json.put("time", time)
        json.put("destinationName", destName)
        json.put("start", start.toString())
        json.put("arriveBy", arriveBy)
        json.put("originName", originName)


        val requestBody = json.toString().toRequestBody(mediaType)
        Log.d("ReqBody", json.toString())
        val request: Request = Request.Builder()
            .url(url + "route")
            .post(requestBody)
            .build()

        val body = client.newCall(request).execute().body?.string()

        val maxLogSize = 1000
        val stringLength = body?.length
        if (stringLength != null) {
            for (i in 0..stringLength / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > body.length) body.length else end
                Log.v("request", body.substring(start, end))
            }
        }

        val type = newParameterizedType(RouteOptions::class.java)
        val moshi = Moshi.Builder()
            .add(RouteAdapter())
            .add(KotlinJsonAdapterFactory())
            .add(CustomDateAdapter())
            .build()

        val adapter: JsonAdapter<RouteOptions> = moshi.adapter(type)
        return adapter.fromJson(body) ?: RouteOptions(emptyList(), emptyList(), emptyList())
    }
}