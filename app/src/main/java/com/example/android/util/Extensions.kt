package com.example.android.util

import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun View.getColor(@ColorRes color: Int) = ContextCompat.getColor(context, color)