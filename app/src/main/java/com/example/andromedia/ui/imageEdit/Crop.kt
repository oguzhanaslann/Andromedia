package com.example.andromedia.ui.imageEdit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Crop(
    modifier: Modifier = Modifier,
    density: Density = Density(LocalContext.current),
    lineWidth: Dp = 2.dp,
    drawGrid: Boolean = true,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }
        var topLeft by remember { mutableStateOf(Offset(0f, 0f)) }
        var size by remember { mutableStateOf(Size(maxWidthPx, maxHeightPx)) }
        val center = remember(topLeft, size) {
            Offset(
                x = topLeft.x + size.width / 2f,
                y = topLeft.y + size.height / 2f
            )
        }
        val lineWidthPx = with(density) { lineWidth.toPx() }

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
                                isTopLeftCorner(xDp, yDp, topLeft) -> {
                                    topLeft = Offset(
                                        x = topLeft.x + dragAmount.x,
                                        y = topLeft.y + dragAmount.y
                                    )

                                    size = Size(
                                        width = size.width - dragAmount.x,
                                        height = size.height - dragAmount.y
                                    )
                                }

                                isTopRightCorner(xDp, yDp, topLeft, size) -> {

                                    topLeft = Offset(
                                        x = topLeft.x,
                                        y = topLeft.y + dragAmount.y
                                    )

                                    size = Size(
                                        width = size.width + dragAmount.x,
                                        height = size.height - dragAmount.y
                                    )
                                }

                                isBottomLeftCorner(xDp, yDp, topLeft, size) -> {
                                    topLeft = Offset(
                                        x = topLeft.x + dragAmount.x,
                                        y = topLeft.y
                                    )

                                    size = Size(
                                        width = size.width - dragAmount.x,
                                        height = size.height + dragAmount.y
                                    )
                                }

                                isBottomRightCorner(xDp, yDp, topLeft, size) -> {
                                    size = Size(
                                        width = size.width + dragAmount.x,
                                        height = size.height + dragAmount.y
                                    )
                                }

                                isMiddle(xDp, yDp, topLeft, size) -> {
                                    topLeft = Offset(
                                        x = topLeft.x + dragAmount.x,
                                        y = topLeft.y + dragAmount.y
                                    )
                                }

                                isLeftEdge(xDp, yDp, topLeft, size) -> {
                                    topLeft = Offset(
                                        x = topLeft.x + dragAmount.x,
                                        y = topLeft.y
                                    )

                                    size = Size(
                                        width = size.width - dragAmount.x,
                                        height = size.height
                                    )
                                }

                                isRightEdge(xDp, yDp, topLeft, size) -> {
                                    size = Size(
                                        width = size.width + dragAmount.x,
                                        height = size.height
                                    )
                                }

                                else -> Unit
                            }
                        }
                    }
            ) {
                drawRect(
                    color = Color.White,
                    topLeft = topLeft,
                    size = size,
                    style = Stroke(lineWidthPx)
                )

                drawLine(
                    color = Color.White,
                    strokeWidth = lineWidthPx,
                    start = Offset(
                        x = topLeft.x + size.width / 3,
                        y = center.y - size.height / 2
                    ),
                    end = Offset(
                        x = topLeft.x + size.width / 3,
                        y = center.y + size.height / 2
                    )
                )

                drawLine(
                    color = Color.White,
                    strokeWidth = lineWidthPx,
                    start = Offset(
                        x = topLeft.x + size.width / 3 * 2,
                        y = center.y - size.height / 2
                    ),
                    end = Offset(
                        x = topLeft.x + size.width / 3 * 2,
                        y = center.y + size.height / 2
                    )
                )

                drawLine(
                    color = Color.White,
                    strokeWidth = lineWidthPx,
                    start = Offset(
                        x = center.x - size.width / 2,
                        y = topLeft.y + size.height / 3
                    ),
                    end = Offset(
                        x = center.x + size.width / 2,
                        y = topLeft.y + size.height / 3
                    )
                )

                drawLine(
                    color = Color.White,
                    strokeWidth = lineWidthPx,
                    start = Offset(
                        x = center.x - size.width / 2,
                        y = topLeft.y + size.height / 3 * 2
                    ),
                    end = Offset(
                        x = center.x + size.width / 2,
                        y = topLeft.y + size.height / 3 * 2
                    )
                )
            }
        }
    }
}

fun PointerInputScope.isTopLeftCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
): Boolean {
    return xDp in topLeft.x.toDp() - 48.dp..topLeft.x.toDp() + 48.dp
            && yDp in topLeft.y.toDp() - 48.dp..topLeft.y.toDp() + 48.dp
}

fun PointerInputScope.isTopRightCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + size.width.toDp() - 48.dp..topLeft.x.toDp() + size.width.toDp() + 48.dp
            && yDp in topLeft.y.toDp() - 48.dp..topLeft.y.toDp() + 48.dp
}

fun PointerInputScope.isBottomLeftCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() - 48.dp..topLeft.x.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + size.height.toDp() - 48.dp..topLeft.y.toDp() + size.height.toDp() + 48.dp
}

fun PointerInputScope.isBottomRightCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + size.width.toDp() - 48.dp..topLeft.x.toDp() + size.width.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + size.height.toDp() - 48.dp..topLeft.y.toDp() + size.height.toDp() + 48.dp
}

fun PointerInputScope.isMiddle(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + 48.dp..topLeft.x.toDp() + size.width.toDp() - 48.dp
            && yDp in topLeft.y.toDp() + 48.dp..topLeft.y.toDp() + size.height.toDp() - 48.dp
}

fun PointerInputScope.isLeftEdge(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() - 48.dp..topLeft.x.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + 48.dp..topLeft.y.toDp() + size.height.toDp() - 48.dp
}

fun PointerInputScope.isRightEdge(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + size.width.toDp() - 48.dp..topLeft.x.toDp() + size.width.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + 48.dp..topLeft.y.toDp() + size.height.toDp() - 48.dp
}