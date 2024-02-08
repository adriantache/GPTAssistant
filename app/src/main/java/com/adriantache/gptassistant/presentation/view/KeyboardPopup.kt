package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KeyboardPopup(
    isExpanded: Boolean,
    onExpand: () -> Unit,
    content: @Composable (FocusRequester) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    var isTutorialTextVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)

        isTutorialTextVisible = true

        delay(2000)

        isTutorialTextVisible = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isExpanded) {
                            focusRequester.freeFocus()
                        }

                        onExpand()
                    },
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_keyboard_24),
                    contentDescription = "Show keyboard",
                    tint = Color.White
                )
            }

            AnimatedVisibility(
                isExpanded,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 500, delayMillis = 200)),
            ) {
                LaunchedEffect(this.transition.isRunning) {
                    if (this@AnimatedVisibility.transition.isRunning) return@LaunchedEffect

                    focusRequester.requestFocus()
                }

                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    content(focusRequester)
                }
            }
        }
    }
}

@Preview
@Composable
fun KeyboardPopupPreview() {
    KeyboardPopup(
        isExpanded = true,
        onExpand = {},
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
        ) {
            Text(modifier = Modifier.padding(16.dp), text = "test")
        }
    }
}

@Preview
@Composable
fun KeyboardPopupPreviewClosed() {
    KeyboardPopup(
        isExpanded = false,
        onExpand = {},
    ) {}
}
