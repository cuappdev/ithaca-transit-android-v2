package com.example.ithaca_transit_android_v2.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.ithaca_transit_android_v2.Repository
import com.google.android.gms.location.*

// Manages the user's location and calls CurrLocationListener
class CurrLocationManager(c: Context, locationManager: LocationManager, activity: Activity) {
    var mContext: Context
    var mActivity: Activity
    var mLocationManager: LocationManager
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        mContext = c
        mActivity = activity
        mLocationManager = locationManager
        locationRequest = LocationRequest.create()
        if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Repository.isPermissionGranted = true
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    Repository.currentLocation = locationResult?.lastLocation
                }
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity)
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
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