package com.craziers.clubpicker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.craziers.clubpicker.viewmodel.MappedFace
import com.craziers.clubpicker.data.Club
import kotlin.math.roundToInt

@Composable
fun FaceOverlayCanvas(
    faces: List<MappedFace>,
    assignedClubs: Map<Int, Club>,
    shuffleClubs: Map<Int, Club>,
    isSpinning: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Face bounding boxes have been removed as per user request
        }

        faces.forEach { face ->
            val clubToShow = if (isSpinning) {
                shuffleClubs[face.id]
            } else {
                assignedClubs[face.id]
            }

            val density = LocalDensity.current
            val xPos = face.bounds.center.x.roundToInt()
            val yPos = with(density) { face.bounds.top.roundToInt() - 20.dp.roundToPx() }

            Box(
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(
                            x = xPos - (placeable.width / 2),
                            y = yPos - placeable.height
                        )
                    }
                }
            ) {
                if (clubToShow != null) {
                    ClubCardOverlay(club = clubToShow, isSpinning = isSpinning)
                } else {
                    Text(
                        text = "Tap spin to start",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .background(Color(0xCC1A1F35), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
