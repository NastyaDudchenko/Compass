package com.sample.compass.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sample.compass.compass.CompassListener
import com.sample.compass.compass.CompassManager
import com.sample.compass.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), CompassListener {

    private lateinit var binding: ActivityMainBinding
    private val compassManager by lazy { CompassManager(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(compassManager)
    }

    override fun onAzimuthChanged(azimuth: Float) {
        binding.compass.compassCustom.startAnimateCompass(azimuth)
        binding.compass.compassAzimuth.text = "${azimuth.roundToInt()}°"
    }
}