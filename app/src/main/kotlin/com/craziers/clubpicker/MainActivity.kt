package com.craziers.clubpicker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.craziers.clubpicker.data.ClubRepository
import com.craziers.clubpicker.ui.CameraScreen
import com.craziers.clubpicker.ui.HistoryScreen
import com.craziers.clubpicker.ui.theme.ClubPickerTheme
import com.craziers.clubpicker.viewmodel.ClubPickerViewModel
import com.craziers.clubpicker.ui.DashboardScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class Screen {
    Camera, Dashboard, History
}

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val repository = ClubRepository(this)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ClubPickerViewModel::class.java)) {
                    return ClubPickerViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        val viewModel = ViewModelProvider(this, factory)[ClubPickerViewModel::class.java]

        setContent {
            ClubPickerTheme {
                @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
                var currentScreen by remember { mutableStateOf(Screen.Camera) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        Screen.Camera -> {
                            CameraScreen(
                                viewModel = viewModel,
                                onOpenDashboard = { currentScreen = Screen.Dashboard },
                                onOpenHistory = { currentScreen = Screen.History }
                            )
                        }
                        Screen.Dashboard -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Camera }
                            )
                        }
                        Screen.History -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Camera }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
