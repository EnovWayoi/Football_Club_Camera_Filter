package com.craziers.clubpicker.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpinButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_pulse")
    val animScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isEnabled) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_scale_anim"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(animScale)
            .size(80.dp)
            .clip(CircleShape)
            .background(if (isEnabled) Color(0xFF39FF14) else Color.Gray)
            .clickable(enabled = isEnabled, onClick = onClick)
    ) {
        Text(
            text = "SPIN",
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
    }
}
