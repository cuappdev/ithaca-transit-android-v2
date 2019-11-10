package com.example.ithaca_transit_android_v2

import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import com.example.ithaca_transit_android_v2.models.Location
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import com.squareup.moshi.Types.newParameterizedType
import android.R
import com.example.ithaca_transit_android_v2.Models.DataLocation
import com.example.ithaca_transit_android_v2.Models.LocationType
import com.example.ithaca_transit_android_v2.models.Coordinate
import com.squareup.moshi.*

/*
 * NetworkUtils include all the networking calls needed
 */
class NetworkUtils {

    val client = OkHttpClient()
    val url = "https://transit-backend.cornellappdev.com/api/v2/"

    // Function that takes in query and returns list of Locations
    fun getSearchedLocations(
        query: String
    ): List<Location> {

        val json = JSONObject()
        json.put("query", query)
        val mediaType = ("application/json; charset=utf-8").toMediaType()
        var locListReturn: List<Location> = emptyList()

        val requestBody = json.toString().toRequestBody(mediaType)

        val request: Request = Request.Builder()
            .url(url + "search")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("networking", "failed search location POST")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val type = newParameterizedType(List::class.java, Location::class.java)
                val moshi = Moshi.Builder()
                    .add(LocationAdapter())
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val adapter: JsonAdapter<List<Location>> = moshi.adapter(type)

                val locList: List<Location>? = (adapter.fromJson(body))

                if (locList != null) {
                    locListReturn = locList
                }

            }
        })

        return locListReturn
    }
}

// Will be moved to a separate class
@JsonClass(generateAdapter = true)
class LocationAdapter {
    class DataLocation(
        @Json(name = "data")
        val data: List<JsonLocation>
    )

    class JsonLocation(
        @Json(name = "type")
        val type: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "lat")
        val lat: Double,
        @Json(name = "long")
        val long: Double,
        @Json(name = "detail")
        val detail: String?
    )

    @FromJson
    private fun fromJson(json: DataLocation): List<Location> {
        var type: LocationType

        val finalLoc = json.data.map { loc ->

            when (loc.type) {
                "busStop" -> type = LocationType.googlePlace
                else -> type = LocationType.busStop
            }

            Location(type, loc.name, Coordinate(loc.lat, loc.long), loc.detail)
        }
        return finalLoc
    }

    @ToJson
    private fun toJson(json: List<Location>): DataLocation {

        val final = json.map { loc ->
            JsonLocation(
                loc.type.toString(),
                loc.name,
                loc.coordinate.latitude,
                loc.coordinate.longitude,
                loc.detail
            )
        }
        return DataLocation(final)
    }
}