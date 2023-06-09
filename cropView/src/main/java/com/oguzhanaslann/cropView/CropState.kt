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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CropState(
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    coroutineScope: CoroutineScope,
    val minSize: Size,
) {
    private val _topLeft = mutableStateOf(topLeft)
    val topLeft by _topLeft

    private val _size = mutableStateOf(size)
    val size by _size


    private var gridAllowedArea by mutableStateOf(size)

    init {
        gridAllowedArea = size

        coroutineScope.launch {
            snapshotFlow { gridAllowedArea }
                .collectLatest(::updateTopAndSizeByAllowedAreaIfNeeded)
        }
    }

    private fun updateTopAndSizeByAllowedAreaIfNeeded(
        allowedArea: Size,
    ) {
        val allowedWidth = allowedArea.width
        val allowedHeight = allowedArea.height
        _topLeft.value = topLeftInAllowedArea(topLeft, allowedWidth, allowedHeight)
        _size.value = sizeInLimits(size, allowedWidth, allowedHeight)
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

        if (currentTopLeft.x + size.width > allowedWidth) {
            currentTopLeft = Offset(allowedWidth - size.width, currentTopLeft.y)
        }

        if (currentTopLeft.y + size.height > allowedHeight) {
            currentTopLeft = Offset(currentTopLeft.x, allowedHeight - size.height)
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
        val currentRatio = size.width / size.height
        val targetRatio = ratio.value
        if (currentRatio == targetRatio) {
            return
        }

        var appliedWidth = size.width
        var appliedHeight: Float

        do {
            val nextHeight = appliedWidth / targetRatio

            val heightBiggerThanAllowedHeight = isHeightBiggerThanAllowedHeight(nextHeight)
            val heightLessThanMinHeight = isHeightLessThanMinHeight(targetHeight = nextHeight)
            val applyNewHeight = !heightBiggerThanAllowedHeight && !heightLessThanMinHeight
            appliedHeight = when {
                heightBiggerThanAllowedHeight -> gridAllowedArea.height - topLeft.y
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
                widthBiggerThanAllowedWidth -> gridAllowedArea.width - topLeft.x
                else -> minSize.width
            }

        } while (true);

        _size.value = Size(appliedWidth, appliedHeight)
    }

    private fun isWidthBiggerThanAllowedWidth(targetWidth: Float): Boolean {
        return targetWidth + topLeft.x > gridAllowedArea.width
    }

    private fun isWidthLessThanMinWidth(targetWidth: Float): Boolean {
        return targetWidth < minSize.width
    }

    //isHeightBiggerThanAllowedHeight
    private fun isHeightBiggerThanAllowedHeight(targetHeight: Float): Boolean {
        return targetHeight + topLeft.y > gridAllowedArea.height
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
