package com.oguzhanaslann.cropView.cropShape.circle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.oguzhanaslann.cropView.cropShape.CropShape
import com.oguzhanaslann.cropView.cropShape.cropState.CropState

class CircleCrop(
    private val circleCropState: CircularCropState,
) : CropShape {
    override val state: CropState
        get() = circleCropState

    @Composable
    override fun content(maxWidthPx: Float, maxHeightPx: Float) {
        val maxSize = Size(
            width = maxWidthPx,
            height = maxHeightPx
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        circleCropState
                            .resize(change, dragAmount, maxSize)
                            .invoke(this)
                    }
                },
            onDraw = {
                drawCircle(
                    center = circleCropState.center,
                    radius = circleCropState.radius,
                    color = Color.White,
                    style = Stroke(width = 2.dp.toPx())
                )

                // dot in the center
                drawCircle(
                    center = circleCropState.center,
                    radius = 3.dp.toPx(),
                    color = Color.White,
                )
            }
        )
    }
}

@Composable
fun rememberCircularCrop(
    circleCropState: CircularCropState = rememberCircularCropState(),
) = remember(circleCropState) {
    CircleCrop(circleCropState = circleCropState)
}