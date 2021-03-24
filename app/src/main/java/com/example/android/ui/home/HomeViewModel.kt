package com.example.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.domain.home.LoadLaunchIntentUseCase
import com.example.android.model.LaunchIntent
import com.example.android.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val loadLaunchIntentUseCase: LoadLaunchIntentUseCase
) : ViewModel() {

  private val _launchIntents: MutableLiveData<List<LaunchIntent>> = MutableLiveData()
  val launchIntents: LiveData<List<LaunchIntent>>
    get() = _launchIntents

  init {
    viewModelScope.launch {
      loadLaunchIntentUseCase(Unit).collect { _launchIntents.postValue(it.data) }
    }
  }
}