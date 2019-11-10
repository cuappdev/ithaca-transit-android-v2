package com.example.ithaca_transit_android_v2.networking

import android.util.Log
import com.example.ithaca_transit_android_v2.models.Location
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.*
import kotlinx.coroutines.async
import kotlin.coroutines.suspendCoroutine

/*
 * NetworkUtils include all the networking calls needed
 */
class NetworkUtils {

    val client = OkHttpClient()
    val url = "https://transit-backend.cornellappdev.com/api/v2/"
    val mediaType = ("application/json; charset=utf-8").toMediaType()


    // Function that takes in query and returns list of Locations
    fun getSearchedLocations(
        query: String
    ): List<Location> {

        val json = JSONObject()
        json.put("query", query)
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
                Log.d("testing", body)
                val type = newParameterizedType(List::class.java, Location::class.java)
                val moshi = Moshi.Builder()
                    .add(LocationAdapter())
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val adapter: JsonAdapter<List<Location>> = moshi.adapter(type)
                Log.d("testing-loc", adapter.fromJson(body).toString())
                val locList: List<Location>? = adapter.fromJson(body)

                locListReturn = locList ?: emptyList()
            }
        }).run {
            return locListReturn
        }
    }
}
