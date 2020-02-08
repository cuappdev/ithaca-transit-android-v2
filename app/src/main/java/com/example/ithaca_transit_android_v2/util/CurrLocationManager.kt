package com.example.ithaca_transit_android_v2.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat

// Manages the user's location and calls CurrLocationListener
class CurrLocationManager(c: Context, locationManager: LocationManager, activity: Activity) {
    var mContext: Context
    var mActivity: Activity
    var mLocationManager: LocationManager

    init {
        mContext = c
        mActivity = activity
        mLocationManager = locationManager
        val locationListener = CurrLocationListener()
        if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            mActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 888
        )
    }
}