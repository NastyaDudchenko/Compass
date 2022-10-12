package com.sample.compass.location

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.GeomagneticField
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.roundToInt

class CompassLocationManager(
    private val context: Context
) : DefaultLifecycleObserver, LocationListenerCompat {

    private val locationManager: LocationManager? by lazy { context.getSystemService() }
    private var currentLocation: Location? = null
    private var targetLocation: Location? = null

    fun setTargetLocation(target: Location) {
        targetLocation = target
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        registerCurrentLocation()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        unregisterCurrentLocation()
    }

    fun getMagneticNorth(azimuth: Float): Float {
        val currentLocation = currentLocation ?: return azimuth
        val targetLocation = targetLocation ?: return azimuth
        var newAzimuth = azimuth

        val geoField = GeomagneticField(
            currentLocation.latitude.toFloat(),
            currentLocation.longitude.toFloat(),
            currentLocation.altitude.toFloat(),
            System.currentTimeMillis()
        )
        newAzimuth += geoField.declination

        val bearing = currentLocation.bearingTo(targetLocation)
        return (newAzimuth - bearing) % FULL_ROTATION
    }

    fun getDistance(): Int {
        val currentLocation = currentLocation ?: return INVALID_VALUE
        val targetLocation = targetLocation ?: return INVALID_VALUE
        val results = FloatArray(1)
        Location.distanceBetween(
            currentLocation.latitude, currentLocation.longitude,
            targetLocation.latitude, targetLocation.longitude,
            results
        )
        return results[0].roundToInt()
    }

    @SuppressLint("MissingPermission")
    private fun registerCurrentLocation() {
        val locationManager = locationManager ?: return

        LocationManagerCompat.requestLocationUpdates(
            locationManager,
            getProvider(),
            LocationRequestCompat.Builder(MILLIS_IN_SEC).build(),
            this,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun unregisterCurrentLocation() {
        val locationManager = locationManager ?: return
        LocationManagerCompat.removeUpdates(locationManager, this)
    }

    private fun getProvider(): String {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        var provider = locationManager?.getBestProvider(criteria, true)
        if (provider.isNullOrBlank()) {
            provider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                LocationManager.FUSED_PROVIDER
            } else {
                LocationManager.GPS_PROVIDER
            }
        }
        return provider
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
    }

    companion object {
        private const val FULL_ROTATION = 360
        private const val INVALID_VALUE = -1
        private const val MILLIS_IN_SEC = 1000 * 1L
    }
}