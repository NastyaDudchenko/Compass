package com.sample.compass.compass.vector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sample.compass.compass.CompassListener

class CompassVectorManager(
    private val context: Context,
    private val listener: CompassListener? = null
) : DefaultLifecycleObserver, SensorEventListener {

    private val sensorManager: SensorManager? by lazy { context.getSystemService<SensorManager>() }

    private val rotationMatrix = FloatArray(ROTATION_MATRIX_SIZE)
    private val orientation = FloatArray(ORIENTATION_SIZE)
    private var azimuth = 0.0

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
            sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun unregisterCompass() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val event = sensorEvent ?: return
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        val orientationValue =
            SensorManager.getOrientation(rotationMatrix, orientation)[0].toDouble()
        azimuth = (Math.toDegrees(orientationValue) + FULL_ROTATION) % FULL_ROTATION
        listener?.onAzimuthChanged(azimuth.toFloat())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /*no-op*/
    }

    companion object {
        private const val FULL_ROTATION = 360
        private const val ROTATION_MATRIX_SIZE = 9
        private const val ORIENTATION_SIZE = 3
    }
}