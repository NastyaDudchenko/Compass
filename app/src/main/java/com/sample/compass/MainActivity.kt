package com.sample.compass

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
        @Suppress("DEPRECATION")
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun initData() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val event = sensorEvent ?: return
        val degree: Int = (event.values[0]).roundToInt()
        val rotateAnimation = RotateAnimation(
            currentDegree, (-degree).toFloat(),
            Animation.RELATIVE_TO_SELF, PIVOT_X_VALUE,
            Animation.RELATIVE_TO_SELF, PIVOT_Y_VALUE
        )

        rotateAnimation.duration = ROTATE_ANIMATION_DURATION
        rotateAnimation.fillAfter = true

        binding.compassIcon.startAnimation(rotateAnimation)
        currentDegree = (-degree).toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    companion object {
        private const val PIVOT_X_VALUE = 0.5F
        private const val PIVOT_Y_VALUE = 0.5F
        private const val ROTATE_ANIMATION_DURATION = 210L
    }
}