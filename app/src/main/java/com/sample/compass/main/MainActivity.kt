package com.sample.compass.main

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.sample.compass.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding

    private var currentDegree = 0F
    private var sensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    private fun initData() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val event = sensorEvent ?: return
        val azimuth: Int = (event.values[0]).roundToInt()
        val rotateAnimation = RotateAnimation(
            currentDegree, (-azimuth).toFloat(),
            Animation.RELATIVE_TO_SELF, PIVOT_X_VALUE,
            Animation.RELATIVE_TO_SELF, PIVOT_Y_VALUE
        )
        currentDegree = (-azimuth).toFloat()

        rotateAnimation.duration = ROTATE_ANIMATION_DURATION
        rotateAnimation.fillAfter = true

        binding.compass.compassCustom.startAnimation(rotateAnimation)
        binding.compass.compassAzimuth.text = "$azimuth°"
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    companion object {
        private const val PIVOT_X_VALUE = 0.5F
        private const val PIVOT_Y_VALUE = 0.5F
        private const val ROTATE_ANIMATION_DURATION = 210L
    }
}