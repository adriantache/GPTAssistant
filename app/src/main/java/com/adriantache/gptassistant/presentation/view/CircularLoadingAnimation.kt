package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Inspired by https://semicolonspace.com/jetpack-compose-loading-animation-2/
@Composable
fun CircularLoadingAnimation(
    color: Color = MaterialTheme.colorScheme.primary,
    animationDelay: Int = 1500,
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = (animationDelay / 3L) * (index + 1))

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = animationDelay, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    circles.forEach { animatable ->
        Box(
            modifier = Modifier
                .scale(scale = animatable.value)
                .size(size = 200.dp)
                .clip(shape = CircleShape)
                .background(color = color.copy(alpha = (1 - animatable.value)))
        ) {}
    }
}
