package com.example.prac14.presentation.viewmodel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class CompassState {
    data class Active(val azimuth: Float) : CompassState()
    object SensorNotAvailable : CompassState()
}

class CompassViewModel : ViewModel() {
    private val _compassState = MutableStateFlow<CompassState>(CompassState.Active(0f))
    val compassState = _compassState.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var sensorManager: SensorManager? = null

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return

            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> System.arraycopy(
                    event.values,
                    0,
                    gravity,
                    0,
                    3
                )

                Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(
                    event.values,
                    0,
                    geomagnetic,
                    0,
                    3
                )
            }

            calculateAzimuth()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun calculateAzimuth() {
        val rotationMatrix = FloatArray(9)
        val inclinationMatrix = FloatArray(9)

        val success = SensorManager.getRotationMatrix(
            rotationMatrix, inclinationMatrix, gravity, geomagnetic
        )

        if (success) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            if (azimuth < 0) azimuth += 360f

            _compassState.value = CompassState.Active(azimuth)
        }
    }

    fun registerSensors(context: Context) {
        sensorManager = context.getSystemService(SensorManager::class.java)

        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer  = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            _compassState.value = CompassState.SensorNotAvailable
            return
        }

        sensorManager?.registerListener(
            sensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        sensorManager?.registerListener(
            sensorListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun unregisterSensors() {
        sensorManager?.unregisterListener(sensorListener)
    }

    override fun onCleared() {
        super.onCleared()
        unregisterSensors()
    }
}