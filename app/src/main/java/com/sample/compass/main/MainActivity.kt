package com.sample.compass.main

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sample.compass.utils.PermissionResultCaller
import com.sample.compass.compass.CompassListener
import com.sample.compass.compass.vector.CompassVectorManager
import com.sample.compass.databinding.ActivityMainBinding
import com.sample.compass.location.CompassLocationManager
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), CompassListener {

    private lateinit var binding: ActivityMainBinding

    private val compassManager by lazy { CompassVectorManager(this, this) }
    private val locationManager by lazy { CompassLocationManager(this) }

    private val permissionResultCaller = PermissionResultCaller(this, this)
    private val permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager.setTargetLocation(TARGET_LOCATION)

        lifecycle.addObserver(compassManager)
        permissionResultCaller.onStart(permissions) {
            if (it) {
                lifecycle.addObserver(locationManager)
            } else {
                Toast.makeText(this, "Need permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAzimuthChanged(azimuth: Float) {
        val targetAzimuth = if (permissionResultCaller.isGranted(permissions)) {
            locationManager.getMagneticNorth(azimuth)
        } else {
            azimuth
        }
        with(binding.compass) {
            compassCustom.startAnimateCompass(targetAzimuth)
            compassAzimuth.text = "${targetAzimuth.roundToInt()}°"
            compassDestination.text = "${locationManager.getDistance()}m"
        }
    }

    companion object {
        private val TARGET_LOCATION = Location("").apply {
            latitude = 0.0
            longitude = 0.0
        }
    }
}