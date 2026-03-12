package com.craziers.clubpicker.ui

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.craziers.clubpicker.camera.CameraManager
import com.craziers.clubpicker.utils.OverlayCoordinateMapper
import com.craziers.clubpicker.viewmodel.ClubPickerViewModel
import com.craziers.clubpicker.viewmodel.MappedFace
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme

@Composable
fun CameraScreen(
    viewModel: ClubPickerViewModel,
    onOpenDashboard: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val detectedFaces by viewModel.detectedFaces.collectAsState()
    val assignedClubs by viewModel.assignedClubs.collectAsState()
    val shuffleClubs by viewModel.shuffleClubs.collectAsState()
    val isSpinning by viewModel.isSpinning.collectAsState()

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val cameraManager = remember { CameraManager(context) }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FIT_CENTER } }

    LaunchedEffect(lensFacing) {
        val cameraProvider = cameraManager.getCameraProvider()
        cameraManager.bindCamera(
            lifecycleOwner = lifecycleOwner,
            cameraProvider = cameraProvider,
            previewView = previewView,
            lensFacing = lensFacing
        ) { faces, imageWidth, imageHeight ->
            val canvasSize = androidx.compose.ui.geometry.Size(
                previewView.width.toFloat(),
                previewView.height.toFloat()
            )
            val isFront = lensFacing == CameraSelector.LENS_FACING_FRONT
            val mapped = faces.map { face ->
                MappedFace(
                    id = face.trackingId ?: face.hashCode(),
                    bounds = OverlayCoordinateMapper.mapRect(
                        boundingBox = face.boundingBox,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                        canvasSize = canvasSize,
                        isFrontCamera = isFront
                    )
                )
            }
            viewModel.onFacesDetected(mapped)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )

        FaceOverlayCanvas(
            faces = detectedFaces,
            assignedClubs = assignedClubs,
            shuffleClubs = shuffleClubs,
            isSpinning = isSpinning
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color(0xAA000000), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "👤 ${detectedFaces.size} face(s)",
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SpinButton(
                    isEnabled = !isSpinning && detectedFaces.isNotEmpty(),
                    onClick = { viewModel.startSpin() }
                )
                
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "made by Craziers",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Support me on Ko-fi",
                        color = Color(0xFF29B6F6),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { uriHandler.openUri("https://ko-fi.com/craziers") }
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Button(onClick = onOpenDashboard) {
                    Text("Dashboard")
                }
                Button(onClick = onOpenHistory) {
                    Text("History")
                }
            }
            
            if (detectedFaces.isEmpty() && !isSpinning) {
                Text(
                    text = "Point camera at face(s)",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
