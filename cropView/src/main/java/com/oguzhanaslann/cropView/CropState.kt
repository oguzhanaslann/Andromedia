package com.oguzhanaslann.cropView

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CropState(
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    coroutineScope: CoroutineScope,
    val minSize: Size,
) : RectangleCropShapeState {
    private val _topLeft = mutableStateOf(topLeft)
    val topLeft_ by _topLeft

    private val _size = mutableStateOf(size)
    val size_ by _size

    override val topLeft: Offset
        get() = _topLeft.value
    override val size: Size
        get() = _size.value

    private var gridAllowedArea by mutableStateOf(size)

    init {
        gridAllowedArea = size

        coroutineScope.launch {
            snapshotFlow { gridAllowedArea }
                .collectLatest(::updateTopAndSizeByAllowedAreaIfNeeded)
        }
    }

    override fun resize(
        change: PointerInputChange,
        dragAmount: Offset,
        maxSize: Size
    ): PointerInputScope.() -> Unit = {

        val xDp = change.position.x.toDp()
        val yDp = change.position.y.toDp()
        val maxWidthPx = maxSize.width
        val maxHeightPx = maxSize.height
        when {
            isTopLeftCorner(xDp, yDp, topLeft) -> {
                setTopLeft(
                    x = boundedNewX(
                        topLeft = topLeft,
                        dragAmount = dragAmount,
                        size = this@CropState.size,
                        maxWidthPx = maxWidthPx
                    ),
                    y = boundedNewY(
                        topLeft = topLeft,
                        dragAmount = dragAmount,
                        size = this@CropState.size,
                        maxHeightPx = maxHeightPx
                    )
                )

                setSize(
                    width = boundedWidth(
                        this@CropState.size.width - dragAmount.x,
                        maxWidthPx
                    ),
                    height = boundedHeight(
                        this@CropState.size.height - dragAmount.y,
                        maxHeightPx
                    )
                )
            }

            isTopRightCorner(xDp, yDp, topLeft, this@CropState.size) -> {
                setTopLeft(
                    x = topLeft.x,
                    y = boundedNewY(
                        topLeft = topLeft,
                        dragAmount = dragAmount,
                        size = this@CropState.size,
                        maxHeightPx = maxHeightPx
                    )
                )

                setSize(
                    width = boundedWidth(
                        this@CropState.size.width + dragAmount.x,
                        maxWidthPx
                    ),
                    height = boundedHeight(
                        this@CropState.size.height - dragAmount.y,
                        maxHeightPx
                    )
                )
            }

            isBottomLeftCorner(xDp, yDp, topLeft, this@CropState.size) -> {
                setTopLeft(
                    x = boundedNewX(
                        topLeft = topLeft,
                        dragAmount = dragAmount,
                        size = this@CropState.size,
                        maxWidthPx = maxWidthPx
                    ),
                    y = topLeft.y
                )

                setSize(
                    width = boundedWidth(
                        this@CropState.size.width - dragAmount.x,
                        maxWidthPx
                    ),
                    height = boundedHeight(
                        this@CropState.size.height + dragAmount.y,
                        maxHeightPx
                    )
                )
            }

            isBottomRightCorner(
                xDp,
                yDp,
                topLeft,
                this@CropState.size
            ) -> {
                setSize(
                    width = boundedWidth(
                        this@CropState.size.width + dragAmount.x,
                        maxWidthPx
                    ),
                    height = boundedHeight(
                        this@CropState.size.height + dragAmount.y,
                        maxHeightPx
                    )
                )
            }

            isMiddle(xDp, yDp, topLeft, this@CropState.size) -> {
                val newX = boundedNewX(
                    topLeft,
                    dragAmount,
                    this@CropState.size,
                    maxWidthPx
                )
                val newY = boundedNewY(
                    topLeft,
                    dragAmount,
                    this@CropState.size,
                    maxHeightPx
                )

                setTopLeft(
                    x = newX,
                    y = newY
                )
            }

            isLeftEdge(xDp, yDp, topLeft, this@CropState.size) -> {
                setTopLeft(
                    x = boundedNewX(
                        topLeft = topLeft,
                        dragAmount = dragAmount,
                        size = this@CropState.size,
                        maxWidthPx = maxWidthPx
                    ),
                    y = topLeft.y
                )

                setSize(
                    width = boundedWidth(
                        this@CropState.size.width - dragAmount.x,
                        maxWidthPx
                    ),
                    height = this@CropState.size.height
                )
            }

            isRightEdge(xDp, yDp, topLeft, this@CropState.size) -> {
                setSize(
                    width = boundedWidth(
                        this@CropState.size.width + dragAmount.x,
                        maxWidthPx
                    ),
                    height = this@CropState.size.height
                )
            }

            else -> Unit
        }
    }


    private fun updateTopAndSizeByAllowedAreaIfNeeded(
        allowedArea: Size,
    ) {
        val allowedWidth = allowedArea.width
        val allowedHeight = allowedArea.height
        _topLeft.value = topLeftInAllowedArea(topLeft_, allowedWidth, allowedHeight)
        _size.value = sizeInLimits(size_, allowedWidth, allowedHeight)
    }

    private fun topLeftInAllowedArea(
        topLeft: Offset,
        allowedWidth: Float,
        allowedHeight: Float,
    ): Offset {
        var currentTopLeft = topLeft

        if (currentTopLeft.x < 0) {
            currentTopLeft = Offset(0f, currentTopLeft.y)
        }

        if (currentTopLeft.y < 0) {
            currentTopLeft = Offset(currentTopLeft.x, 0f)
        }

        if (currentTopLeft.x + size_.width > allowedWidth) {
            currentTopLeft = Offset(allowedWidth - size_.width, currentTopLeft.y)
        }

        if (currentTopLeft.y + size_.height > allowedHeight) {
            currentTopLeft = Offset(currentTopLeft.x, allowedHeight - size_.height)
        }

        return currentTopLeft
    }

    private fun sizeInLimits(
        size: Size,
        allowedWidth: Float,
        allowedHeight: Float,
    ): Size {
        var currentSize = size

        if (currentSize.width > allowedWidth) {
            currentSize = Size(allowedWidth, currentSize.height)
        }

        if (currentSize.height > allowedHeight) {
            currentSize = Size(currentSize.width, allowedHeight)
        }

        if (currentSize.width < minSize.width) {
            currentSize = Size(minSize.width, currentSize.height)
        }

        if (currentSize.height < minSize.height) {
            currentSize = Size(currentSize.width, minSize.height)
        }

        return currentSize
    }


    internal fun setTopLeft(x: Float, y: Float) {
        val newTopLeft =
            topLeftInAllowedArea(
                topLeft = Offset(x, y),
                allowedWidth = gridAllowedArea.width,
                allowedHeight = gridAllowedArea.height
            )
        _topLeft.value = newTopLeft
    }

    internal fun setSize(width: Float, height: Float) {
        val newSize = sizeInLimits(
            size = Size(width, height),
            allowedWidth = gridAllowedArea.width,
            allowedHeight = gridAllowedArea.height
        )
        _size.value = newSize
    }

    internal fun setGridAllowedArea(width: Float, height: Float) {
        gridAllowedArea = Size(width, height)
    }

    fun setAspectRatio(ratio: Ratio) {
        val currentRatio = size_.width / size_.height
        val targetRatio = ratio.value
        if (currentRatio == targetRatio) {
            return
        }

        var appliedWidth = size_.width
        var appliedHeight: Float

        do {
            val nextHeight = appliedWidth / targetRatio

            val heightBiggerThanAllowedHeight = isHeightBiggerThanAllowedHeight(nextHeight)
            val heightLessThanMinHeight = isHeightLessThanMinHeight(targetHeight = nextHeight)
            val applyNewHeight = !heightBiggerThanAllowedHeight && !heightLessThanMinHeight
            appliedHeight = when {
                heightBiggerThanAllowedHeight -> gridAllowedArea.height - topLeft_.y
                heightLessThanMinHeight -> minSize.height
                else -> nextHeight
            }

            if (applyNewHeight) {
                break
            }

            appliedWidth = appliedHeight * targetRatio

            val widthBiggerThanAllowedWidth = isWidthBiggerThanAllowedWidth(appliedWidth)
            val widthLessThanMinWidth = isWidthLessThanMinWidth(targetWidth = appliedWidth)
            val applyNewWidth = !widthBiggerThanAllowedWidth && !widthLessThanMinWidth

            if (applyNewWidth) {
                break
            }

            appliedWidth = when {
                widthBiggerThanAllowedWidth -> gridAllowedArea.width - topLeft_.x
                else -> minSize.width
            }

        } while (true);

        _size.value = Size(appliedWidth, appliedHeight)
    }

    private fun isWidthBiggerThanAllowedWidth(targetWidth: Float): Boolean {
        return targetWidth + topLeft_.x > gridAllowedArea.width
    }

    private fun isWidthLessThanMinWidth(targetWidth: Float): Boolean {
        return targetWidth < minSize.width
    }

    //isHeightBiggerThanAllowedHeight
    private fun isHeightBiggerThanAllowedHeight(targetHeight: Float): Boolean {
        return targetHeight + topLeft_.y > gridAllowedArea.height
    }

    private fun isHeightLessThanMinHeight(targetHeight: Float): Boolean {
        return targetHeight < minSize.height
    }
}

@Composable
fun rememberCropState(
    size: Size = Size.Zero,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    minSize: Size = Size(100.dp.toPx(), 100.dp.toPx()),
) = remember(size) {
    CropState(
        topLeft = Offset.Zero,
        size = size,
        coroutineScope = coroutineScope,
        minSize = minSize
    )
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