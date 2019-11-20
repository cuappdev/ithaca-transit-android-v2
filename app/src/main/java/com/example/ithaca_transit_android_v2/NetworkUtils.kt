package com.example.ithaca_transit_android_v2.networking

import android.util.Log
import com.example.ithaca_transit_android_v2.CustomDateAdapter
import com.example.ithaca_transit_android_v2.LocationAdapter
//import com.example.ithaca_transit_android_v2.RouteAdapter
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
        start: Coordinate,
        end: Coordinate,
        time: Double,
        arriveBy: Boolean = true,
        destName : String
    ): RouteOptions {

        val json = JSONObject()
        json.put("start", start.toString())
        json.put("end", end.toString())
        json.put("time", time)
        json.put("arriveBy", arriveBy)
        json.put("destinationName", destName)

        val requestBody = json.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url + "route")
            .post(requestBody)
            .build()

        val body = client.newCall(request).execute().body?.string()
        Log.d("testing", body)
        val response = JSONObject(body)
        val arr = response.get("data")
        Log.d("testing", arr.toString())

        val type = newParameterizedType(RouteOptions::class.java)
        val moshi = Moshi.Builder()
            .add(CustomDateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
//
        val adapter: JsonAdapter<RouteOptions> = moshi.adapter(type)

        return adapter.fromJson(arr.toString()) ?: RouteOptions(emptyList(), emptyList(), emptyList())
//        return RouteOptions(emptyList(), emptyList(), emptyList())
    }
}