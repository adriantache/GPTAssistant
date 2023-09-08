package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @param amplitudePercent - voice amplitude, in decimal percentage (0.5f = 50%)
 */
@Composable
fun MicrophoneInputDisplay(
    modifier: Modifier = Modifier,
    amplitudePercent: Float,

    ) {
    var canvasHeight by remember { mutableStateOf(0) }
    var canvasWidth by remember { mutableStateOf(0) }

    Canvas(modifier = modifier
        .fillMaxSize()
        .onGloballyPositioned {
            canvasWidth = it.size.width
            canvasHeight = it.size.height
        }
        .background(Color.White)
    ) {
        val width = canvasWidth.toFloat()
        val height = canvasHeight.toFloat()
        val heightUnit = height / 5

        val rectWidth = width / 10

        val lineStart = heightUnit + heightUnit - heightUnit * amplitudePercent
        val lineEnd = heightUnit + 2 * heightUnit * amplitudePercent

        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(width / 2 - rectWidth / 2, lineStart),
            size = Size(rectWidth, lineEnd),
            cornerRadius = CornerRadius(100f),
        )

        val smallHeightUnit = heightUnit / 2
        val smallHeightPadding = heightUnit + smallHeightUnit / 2
        val smallLineStart = smallHeightPadding + smallHeightUnit + smallHeightUnit - smallHeightUnit * amplitudePercent
        val smallLineEnd = smallHeightUnit + 2 * smallHeightUnit * amplitudePercent

        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(width / 3 - rectWidth / 2, smallLineStart),
            size = Size(rectWidth, smallLineEnd),
            cornerRadius = CornerRadius(100f),
        )

        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(width / 3 * 2 - rectWidth / 2, smallLineStart),
            size = Size(rectWidth, smallLineEnd),
            cornerRadius = CornerRadius(100f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MicrophoneInputDisplayPreview() {
    MicrophoneInputDisplay(
        modifier = Modifier
            .padding(16.dp)
            .requiredSize(50.dp),
        amplitudePercent = 0.5f
    )
}
