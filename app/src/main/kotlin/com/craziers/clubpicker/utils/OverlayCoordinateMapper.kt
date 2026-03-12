package com.craziers.clubpicker.utils

import android.graphics.Rect
import androidx.compose.ui.geometry.Size

object OverlayCoordinateMapper {
    fun mapRect(
        boundingBox: Rect,
        imageWidth: Int,
        imageHeight: Int,
        canvasSize: Size,
        isFrontCamera: Boolean = true
    ): androidx.compose.ui.geometry.Rect {
        val scaleX = canvasSize.width / imageWidth.toFloat()
        val scaleY = canvasSize.height / imageHeight.toFloat()
        val scale = maxOf(scaleX, scaleY)

        val scaledImageWidth = imageWidth * scale
        val scaledImageHeight = imageHeight * scale
        val offsetX = (canvasSize.width - scaledImageWidth) / 2f
        val offsetY = (canvasSize.height - scaledImageHeight) / 2f

        var left = boundingBox.left * scale + offsetX
        var right = boundingBox.right * scale + offsetX
        val top = boundingBox.top * scale + offsetY
        val bottom = boundingBox.bottom * scale + offsetY

        if (isFrontCamera) {
            val copyLeft = left
            left = canvasSize.width - right
            right = canvasSize.width - copyLeft
        }

        return androidx.compose.ui.geometry.Rect(
            left = left,
            top = top,
            right = right,
            bottom = bottom
        )
    }
}
