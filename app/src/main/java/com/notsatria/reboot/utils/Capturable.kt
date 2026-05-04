package com.notsatria.reboot.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.launch

@Composable
fun Capturable(
    modifier: Modifier = Modifier,
    controller: CaptureController,
    onCaptured: (ImageBitmap) -> Unit,
    content: @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier
            .drawWithCache {
                if (controller.isCapturing) {
                    onDrawWithContent {
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                        coroutineScope.launch {
                            val bitmapResult = graphicsLayer.toImageBitmap()
                            onCaptured(bitmapResult)
                        }
                        controller.stopCapture()
                    }
                } else {
                    onDrawWithContent {
                        drawContent()
                    }
                }
            }) {
        content()
    }
}

class CaptureController {
    private var _isCapturing = mutableStateOf(false)
    val isCapturing: Boolean
        get() = _isCapturing.value

    fun capture() {
        _isCapturing.value = true
    }

    fun stopCapture() {
        _isCapturing.value = false
    }
}

@Composable
fun rememberCaptureController(modifier: Modifier = Modifier): CaptureController {
    return remember { CaptureController() }
}