package com.example.android.util

import android.util.Property

abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name)
