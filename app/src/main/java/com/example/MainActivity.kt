package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FidelViewModel
import com.example.ui.screens.MainAppContainer
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: FidelViewModel = viewModel()
      val progress by viewModel.userProgress.collectAsState()
      val lifecycleOwner = LocalLifecycleOwner.current

      DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
          when (event) {
            Lifecycle.Event.ON_START -> {
              if (viewModel.bgMusicEnabled.value) {
                viewModel.startBackgroundMusic()
              }
            }
            Lifecycle.Event.ON_STOP -> {
              viewModel.stopBackgroundMusic()
            }
            else -> {}
          }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
          lifecycleOwner.lifecycle.removeObserver(observer)
          viewModel.stopBackgroundMusic()
        }
      }

      MyApplicationTheme(highContrast = progress.highContrast) {
        Surface(modifier = Modifier.fillMaxSize()) {
          MainAppContainer(viewModel = viewModel)
        }
      }
    }
  }
}
