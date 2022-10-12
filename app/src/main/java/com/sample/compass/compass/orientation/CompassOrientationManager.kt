package com.sample.compass.compass.orientation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sample.compass.compass.CompassListener

class CompassOrientationManager(
    private val context: Context,
    private val listener: CompassListener? = null
) : DefaultLifecycleObserver, SensorEventListener {

    private val sensorManager: SensorManager? by lazy { context.getSystemService<SensorManager>() }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        registerCompass()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        unregisterCompass()
    }

    private fun registerCompass() {
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun unregisterCompass() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val event = sensorEvent ?: return
        val azimuth: Float = event.values[0]
        listener?.onAzimuthChanged(azimuth)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /*no-op*/
    }
}