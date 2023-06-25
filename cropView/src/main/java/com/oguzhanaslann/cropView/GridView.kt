package com.oguzhanaslann.cropView

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oguzhanaslann.cropView.util.cropped

interface CropShapeState {
    fun resize(
        change: PointerInputChange,
        dragAmount: Offset,
        maxSize: Size,
    ): PointerInputScope.() -> Unit

    fun crop(bitmap: Bitmap): Bitmap

}

interface RectangleCropShapeState : CropShapeState {
    val topLeft: Offset
    val size: Size

    override fun crop(bitmap: Bitmap): Bitmap {
        return bitmap.cropped(
            topLeftX = topLeft.x,
            topLeftY = topLeft.y,
            width = size.width,
            height = size.height
        )
    }
}

@Composable
internal fun GridView(
    cropState: RectangleCropShapeState,
    maxSize: Size,
    lineWidth: Dp = 2.dp,
    onDrawGrid: DrawScope.() -> Unit = {},
) {
    val center = remember(cropState.topLeft, cropState.size) {
        Offset(
            x = cropState.topLeft.x + cropState.size.width / 2f,
            y = cropState.topLeft.y + cropState.size.height / 2f
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    cropState
                        .resize(change, dragAmount, maxSize)
                        .invoke(this)
                }
            }
    ) {
        val lineWidthPx = lineWidth.toPx()

        drawRect(
            color = Color.White,
            topLeft = cropState.topLeft,
            size = cropState.size,
            style = Stroke(lineWidthPx)
        )

        drawLine(
            color = Color.White,
            strokeWidth = lineWidthPx,
            start = Offset(
                x = cropState.topLeft.x + cropState.size.width / 3,
                y = center.y - cropState.size.height / 2
            ),
            end = Offset(
                x = cropState.topLeft.x + cropState.size.width / 3,
                y = center.y + cropState.size.height / 2
            )
        )

        drawLine(
            color = Color.White,
            strokeWidth = lineWidthPx,
            start = Offset(
                x = cropState.topLeft.x + cropState.size.width / 3 * 2,
                y = center.y - cropState.size.height / 2
            ),
            end = Offset(
                x = cropState.topLeft.x + cropState.size.width / 3 * 2,
                y = center.y + cropState.size.height / 2
            )
        )

        drawLine(
            color = Color.White,
            strokeWidth = lineWidthPx,
            start = Offset(
                x = center.x - cropState.size.width / 2,
                y = cropState.topLeft.y + cropState.size.height / 3
            ),
            end = Offset(
                x = center.x + cropState.size.width / 2,
                y = cropState.topLeft.y + cropState.size.height / 3
            )
        )

        drawLine(
            color = Color.White,
            strokeWidth = lineWidthPx,
            start = Offset(
                x = center.x - cropState.size.width / 2,
                y = cropState.topLeft.y + cropState.size.height / 3 * 2
            ),
            end = Offset(
                x = center.x + cropState.size.width / 2,
                y = cropState.topLeft.y + cropState.size.height / 3 * 2
            )
        )

        onDrawGrid()
    }
}