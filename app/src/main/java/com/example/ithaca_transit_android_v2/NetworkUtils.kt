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

    fun getSearchedLocations(
        client: OkHttpClient,
        url: String,
        query: String
    ) {

        val json = JSONObject()
        json.put("query", query)
        val mediaType = ("application/json; charset=utf-8").toMediaType()

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
                println(body)

                val type = newParameterizedType(List::class.java, Location::class.java)
                val moshi = Moshi.Builder()
                    .add(LocationAdapter())
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val adapter: JsonAdapter<List<Location>> = moshi.adapter(type)

                val locList : List<Location>? = (adapter.fromJson(body))
                // Test to see if we are successfully wrapping json into objects
                Log.d("name", locList.toString())
            }
        })
    }
}

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

//    @FromJson private fun fromJson(json: JsonLocation) : Location {
//        return Location(json.name, Coordinate(json.lat, json.long), json.detail)
//    }
//
//    @ToJson private fun toJson(loc: Location): JsonLocation {
//        return JsonLocation(loc.name, loc.coordinate.latitude, loc.coordinate.longitude, loc.detail)
//    }

    @FromJson
    private fun fromJson(json: DataLocation): List<Location> {
//        var finalLoc : Array<Location> = arrayOf()
        var type : LocationType


        val finalLoc = json.data.map { loc ->
            if(loc.type.equals("busStop")){
                type = LocationType.BUSSTOP
            }
            else{
                type = LocationType.GOOGLEPLACE
            }

            Location(type, loc.name, Coordinate(loc.lat, loc.long), loc.detail)
        }
        return finalLoc
    }

    @ToJson
    private fun toJson(json: List<Location>): DataLocation {

        val final = json.map { loc ->
            JsonLocation("Nothing", loc.name, loc.coordinate.latitude, loc.coordinate.longitude, loc.detail)
        }
        return DataLocation(final)
    }

}