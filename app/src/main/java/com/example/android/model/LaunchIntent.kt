package com.example.android.model

import android.content.Intent
import androidx.recyclerview.widget.DiffUtil

data class LaunchIntent(val label: String, val intent: Intent) {
    companion object {

        val Diff = object : DiffUtil.ItemCallback<LaunchIntent>() {
            override fun areItemsTheSame(oldLaunch: LaunchIntent, newLaunch: LaunchIntent): Boolean {
                return oldLaunch.label == newLaunch.label
            }

            override fun areContentsTheSame(oldLaunch: LaunchIntent, newLaunch: LaunchIntent): Boolean {
                return oldLaunch == newLaunch
            }
        }
    }
}
