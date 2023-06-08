package com.example.andromedia.ui.imageEdit

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Dp.toPx(): Float {
    val density = Density(LocalContext.current)
    return with(density) { toPx() }
}

class CropState(
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    val lineWidth: Dp = 2.dp,
) {
    var topLeft by mutableStateOf(topLeft)
    var size by mutableStateOf(size)
}

@Composable
fun rememberCropState(
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    lineWidth: Dp = 2.dp,
) = remember(topLeft, size, lineWidth) {
    CropState(
        topLeft = topLeft,
        size = size,
        lineWidth = lineWidth
    )
}

@Composable
fun Crop(
    modifier: Modifier = Modifier,
    cropState: CropState = rememberCropState(),
    drawGrid: Boolean = true,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = maxWidth.toPx()
        val maxHeightPx = maxHeight.toPx()

        LaunchedEffect(maxWidthPx, maxHeightPx) {
            if (cropState.size == Size.Zero) {
                cropState.size = Size(maxWidthPx, maxHeightPx)
            }
        }
        val center = remember(cropState.topLeft, cropState.size) {
            Offset(
                x = cropState.topLeft.x + cropState.size.width / 2f,
                y = cropState.topLeft.y + cropState.size.height / 2f
            )
        }

        content()
        AnimatedVisibility(
            visible = drawGrid,
            modifier = Modifier
                .size(
                    width = maxWidth,
                    height = maxHeight
                )
                .align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            val xDp = change.position.x.toDp()
                            val yDp = change.position.y.toDp()
                            when {
                                isTopLeftCorner(xDp, yDp, cropState.topLeft) -> {
                                    cropState.topLeft = Offset(
                                        x = boundedNewX(
                                            topLeft = cropState.topLeft,
                                            dragAmount = dragAmount,
                                            size = cropState.size,
                                            maxWidthPx = maxWidthPx
                                        ),
                                        y = boundedNewY(
                                            topLeft = cropState.topLeft,
                                            dragAmount = dragAmount,
                                            size = cropState.size,
                                            maxHeightPx = maxHeightPx
                                        )
                                    )

                                    cropState.size = Size(
                                        width = boundedWidth(
                                            cropState.size.width - dragAmount.x,
                                            maxWidthPx
                                        ),
                                        height = boundedHeight(
                                            cropState.size.height - dragAmount.y,
                                            maxHeightPx
                                        )
                                    )
                                }

                                isTopRightCorner(xDp, yDp, cropState.topLeft, cropState.size) -> {

                                    cropState.topLeft = Offset(
                                        x = cropState.topLeft.x,
                                        y = boundedNewY(
                                            topLeft = cropState.topLeft,
                                            dragAmount = dragAmount,
                                            size = cropState.size,
                                            maxHeightPx = maxHeightPx
                                        )
                                    )

                                    cropState.size = Size(
                                        width = boundedWidth(
                                            cropState.size.width + dragAmount.x,
                                            maxWidthPx
                                        ),
                                        height = boundedHeight(
                                            cropState.size.height - dragAmount.y,
                                            maxHeightPx
                                        )
                                    )
                                }

                                isBottomLeftCorner(xDp, yDp, cropState.topLeft, cropState.size) -> {
                                    cropState.topLeft = Offset(
                                        x = boundedNewX(
                                            topLeft = cropState.topLeft,
                                            dragAmount = dragAmount,
                                            size = cropState.size,
                                            maxWidthPx = maxWidthPx
                                        ),
                                        y = cropState.topLeft.y
                                    )

                                    cropState.size = Size(
                                        width = boundedWidth(
                                            cropState.size.width - dragAmount.x,
                                            maxWidthPx
                                        ),
                                        height = boundedHeight(
                                            cropState.size.height + dragAmount.y,
                                            maxHeightPx
                                        )
                                    )
                                }

                                isBottomRightCorner(
                                    xDp,
                                    yDp,
                                    cropState.topLeft,
                                    cropState.size
                                ) -> {
                                    cropState.size = Size(
                                        width = boundedWidth(
                                            cropState.size.width + dragAmount.x,
                                            maxWidthPx
                                        ),
                                        height = boundedHeight(
                                            cropState.size.height + dragAmount.y,
                                            maxHeightPx
                                        )
                                    )
                                }

                                isMiddle(xDp, yDp, cropState.topLeft, cropState.size) -> {
                                    val newX = boundedNewX(
                                        cropState.topLeft,
                                        dragAmount,
                                        cropState.size,
                                        maxWidthPx
                                    )
                                    val newY = boundedNewY(
                                        cropState.topLeft,
                                        dragAmount,
                                        cropState.size,
                                        maxHeightPx
                                    )

                                    cropState.topLeft = Offset(
                                        x = newX,
                                        y = newY
                                    )
                                }

                                isLeftEdge(xDp, yDp, cropState.topLeft, cropState.size) -> {
                                    cropState.topLeft = Offset(
                                        x = boundedNewX(
                                            topLeft = cropState.topLeft,
                                            dragAmount = dragAmount,
                                            size = cropState.size,
                                            maxWidthPx = maxWidthPx
                                        ),
                                        y = cropState.topLeft.y
                                    )

                                    cropState.size = Size(
                                        width = boundedWidth(
                                            cropState.size.width - dragAmount.x,
                                            maxWidthPx
                                        ),
                                        height = cropState.size.height
                                    )
                                }

                                isRightEdge(xDp, yDp, cropState.topLeft, cropState.size) -> {
                                    cropState.size = Size(
                                        width = boundedWidth(
                                            cropState.size.width + dragAmount.x,
                                            maxWidthPx
                                        ),
                                        height = cropState.size.height
                                    )
                                }

                                else -> Unit
                            }
                        }
                    }
            ) {
                val lineWidthPx = cropState.lineWidth.toPx()

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
            }
        }
    }
}

private fun boundedNewX(
    topLeft: Offset,
    dragAmount: Offset,
    size: Size,
    maxWidthPx: Float,
) = when {
    topLeft.x + dragAmount.x < 0 -> 0f
    topLeft.x + size.width + dragAmount.x > maxWidthPx -> maxWidthPx - size.width
    else -> topLeft.x + dragAmount.x
}

private fun boundedNewY(
    topLeft: Offset,
    dragAmount: Offset,
    size: Size,
    maxHeightPx: Float,
) = when {
    topLeft.y + dragAmount.y < 0 -> 0f
    topLeft.y + size.height + dragAmount.y > maxHeightPx -> maxHeightPx - size.height
    else -> topLeft.y + dragAmount.y
}

private fun boundedWidth(
    newWidth: Float,
    maxWidthPx: Float,
) = when {
    newWidth < 0 -> 0f
    newWidth > maxWidthPx -> maxWidthPx
    else -> newWidth
}

private fun boundedHeight(
    newHeight: Float,
    maxHeightPx: Float,
) = when {
    newHeight < 0 -> 0f
    newHeight > maxHeightPx -> maxHeightPx
    else -> newHeight
}

@Preview(showBackground = true)
@Composable
fun previewCrop() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Crop {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                color = Color.Gray,
                content = {}
            )
        }
    }
}
