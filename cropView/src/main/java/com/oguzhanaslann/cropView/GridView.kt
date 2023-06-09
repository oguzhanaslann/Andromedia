package com.oguzhanaslann.cropView

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun GridView(
    cropState: CropState,
    maxWidthPx: Float,
    maxHeightPx: Float,
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
                    val xDp = change.position.x.toDp()
                    val yDp = change.position.y.toDp()
                    when {
                        isTopLeftCorner(xDp, yDp, cropState.topLeft) -> {
                            cropState.setTopLeft(
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

                            cropState.setSize(
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

                            cropState.setTopLeft(
                                x = cropState.topLeft.x,
                                y = boundedNewY(
                                    topLeft = cropState.topLeft,
                                    dragAmount = dragAmount,
                                    size = cropState.size,
                                    maxHeightPx = maxHeightPx
                                )
                            )

                            cropState.setSize(
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
                            cropState.setTopLeft(
                                x = boundedNewX(
                                    topLeft = cropState.topLeft,
                                    dragAmount = dragAmount,
                                    size = cropState.size,
                                    maxWidthPx = maxWidthPx
                                ),
                                y = cropState.topLeft.y
                            )

                            cropState.setSize(
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
                            cropState.setSize(
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

                            cropState.setTopLeft(
                                x = newX,
                                y = newY
                            )
                        }

                        isLeftEdge(xDp, yDp, cropState.topLeft, cropState.size) -> {
                            cropState.setTopLeft(
                                x = boundedNewX(
                                    topLeft = cropState.topLeft,
                                    dragAmount = dragAmount,
                                    size = cropState.size,
                                    maxWidthPx = maxWidthPx
                                ),
                                y = cropState.topLeft.y
                            )

                            cropState.setSize(
                                width = boundedWidth(
                                    cropState.size.width - dragAmount.x,
                                    maxWidthPx
                                ),
                                height = cropState.size.height
                            )
                        }

                        isRightEdge(xDp, yDp, cropState.topLeft, cropState.size) -> {
                            cropState.setSize(
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