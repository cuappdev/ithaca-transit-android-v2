package com.example.ithaca_transit_android_v2
import android.util.Log
import com.example.ithaca_transit_android_v2.models.*
import com.example.ithaca_transit_android_v2.models.tracking.BusInformation
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/*
 * NetworkUtils include all the networking calls needed
 */
class NetworkUtils {

    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS) // write timeout
        .readTimeout(15, TimeUnit.SECONDS) // read timeout
        .build()
    val url = "https://transit-testflight.cornellappdev.com/api/v2/"
    val mediaType = ("application/json; charset=utf-8").toMediaType()

    // Function that takes in query and returns list of Locations
    fun getSearchedLocations(query: String): List<Location> {
        val json = JSONObject()
        json.put("query", query)
        val requestBody = json.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url + "appleSearch")
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
        destName: String
    ): RouteOptions {

        val json = JSONObject()
        json.put("end", end.toString())
        json.put("time", time)
        json.put("destinationName", destName)
        json.put("start", start.toString())
        json.put("arriveBy", arriveBy)
        json.put("originName", "Current Location")

        val requestBody = json.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url + "route")
            .post(requestBody)
            .build()

        val body = client.newCall(request).execute().body?.string()
        val response = JSONObject(body)
        val arr = response.get("data")

        val type = newParameterizedType(RouteOptions::class.java)
        val moshi = Moshi.Builder()
            .add(CustomDateAdapter())
            .add(RouteAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter: JsonAdapter<RouteOptions> = moshi.adapter(type)

        return adapter.fromJson(arr.toString()) ?: RouteOptions(
            emptyList(),
            emptyList(),
            emptyList()
        )
    }

    fun getBusCoords(busDataList: List<BusInformation>) {
        val busJsonArr = JSONArray()
        for(busInfo in busDataList) {
            if (busInfo.tripId != null && busInfo.routeId != null) {
                val busJson = JSONObject()
                busJson.put("tripId", busInfo.tripId)
                busJson.put("routeId", busInfo.routeId)
                busJsonArr.put(busJson)
            }
        }
        val json = JSONObject()
        json.put("data", busJsonArr)
        Log.i("qwerty", json.toString())

        val requestBody = json.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url + "tracking")
            .post(requestBody)
            .build()

        val body = client.newCall(request).execute().body?.string()
        val response = JSONObject(body!!)
        val arr = response.get("data")
        Log.i("qwerty", "The server returned: "+response.toString())

    }

    fun applyDelayRoutes(routes: List<Route>): ArrayList<Int>{
        var delays = arrayListOf<Int>()
        for (i in routes){
            delays.add(getDelay(i))
        }
        return delays
    }

    fun getDelay(route: Route):Int {
        //getting StopID
        var stopID = String()
        var tripID = String()
        for (i in route.directions) {
            if (i.type != DirectionType.WALK) {
                stopID = i.busStops[0].stopID
                tripID = i.tripIdentifiers?.get(0) ?:
                break
            } else {break}
        }

        val arr = JSONArray()
        val infoJSON = JSONObject()
        infoJSON.put("stopID", stopID)
        infoJSON.put("tripID", tripID)
        arr.put(infoJSON)
        val json = JSONObject()
        json.put("data", json)

        val requestBody = json.toString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(url + "tracking")
            .post(requestBody)
            .build()
        val body = client.newCall(request).execute().body?.string()
        val response = JSONObject(body!!)
        Log.i("qwerty", "The server returned: "+response.toString())

        //not sure if this works
        return Integer.parseInt(response.toString())
    }


}