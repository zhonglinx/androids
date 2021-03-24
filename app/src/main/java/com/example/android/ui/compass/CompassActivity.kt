package com.example.android.ui.compass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.databinding.ActivityCompassBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompassActivity : AppCompatActivity() {

  private lateinit var binding: ActivityCompassBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityCompassBinding.inflate(layoutInflater).also { setContentView(it.root) }
  }
}