package com.example.ithaca_transit_android_v2

import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import com.example.ithaca_transit_android_v2.models.Location
import com.example.ithaca_transit_android_v2.models.LocationJsonAdapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import com.squareup.moshi.Types.newParameterizedType
import android.R
import androidx.core.app.Person
import com.example.ithaca_transit_android_v2.Models.DataLocation
import com.squareup.moshi.Types


/*
 * NetworkUtils include all the networking calls needed
 */
class NetworkUtils {

    fun getSearchedLocations(
        client : OkHttpClient,
        url : String,
        query : String
    ){

        val json = "{ \"query\": \"$query\"}"
        val mediaType = ("application/json; charset=utf-8").toMediaType()

        val reqBody : RequestBody = RequestBody.create(
            mediaType,json)

        val request: Request = Request.Builder()
            .url(url + "search")
            .post(reqBody)
            .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val adapter = Moshi.Builder().build().adapter(DataLocation::class.java)
                val loc : DataLocation = (adapter.fromJson(body) as DataLocation)
                // Test to see if we are successfully wrapping json into objects
                Log.d("name","${loc.data.get(1).lat}")
            }
    })
    }
}










