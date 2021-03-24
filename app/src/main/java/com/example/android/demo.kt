package com.example.android

import android.util.FloatProperty
import android.util.Property
import android.view.View

object demo {
  val SCALE_X: Property<View, Float> = object : FloatProperty<View>("scaleX") {
    override fun setValue(`object`: View, value: Float) {
      `object`.scaleX = value
    }

    override fun get(`object`: View): Float {
      return `object`.scaleX
    }
  }
}