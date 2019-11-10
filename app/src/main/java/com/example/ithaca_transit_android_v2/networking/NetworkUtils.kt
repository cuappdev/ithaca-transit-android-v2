package com.example.ithaca_transit_android_v2.networking

import com.example.ithaca_transit_android_v2.models.Location
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.*

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
        val locList: List<Location> = adapter.fromJson(body) ?: emptyList()
        return locList
    }
}
