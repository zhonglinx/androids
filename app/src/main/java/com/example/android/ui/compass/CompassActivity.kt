package com.example.android.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.databinding.ActivityCompassBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class CompassActivity : AppCompatActivity(), SensorEventListener {

  private lateinit var binding: ActivityCompassBinding

  private lateinit var sensorManager: SensorManager
  private lateinit var sensor: Sensor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCompassBinding.inflate(layoutInflater).also { setContentView(it.root) }

    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
  }

  override fun onResume() {
    super.onResume()
    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
  }

  override fun onPause() {
    sensorManager.unregisterListener(this)
    super.onPause()
  }

  override fun onSensorChanged(event: SensorEvent) {
    binding.compass.degree = -event.values[0]
  }

  override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}