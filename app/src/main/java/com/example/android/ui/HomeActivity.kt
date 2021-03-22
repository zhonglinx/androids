package com.example.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.databinding.LaunchIntentItemBinding
import com.example.android.model.LaunchIntent
import com.example.android.ui.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private val adapter = LaunchIntentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<RecyclerView>(R.id.launch_intents).apply {
            adapter = this@HomeActivity.adapter
        }

        viewModel.launchIntents.observe(this) { launchIntents ->
            adapter.submitList(launchIntents)
        }
    }

}

class LaunchIntentAdapter : ListAdapter<LaunchIntent, LaunchIntentViewHolder>(LaunchIntent.Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchIntentViewHolder {
        return LaunchIntentViewHolder(
            LaunchIntentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LaunchIntentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class LaunchIntentViewHolder(
    private val binding: LaunchIntentItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LaunchIntent) {
        binding.apply {
            model = item
            executePendingBindings()
        }
    }

}
