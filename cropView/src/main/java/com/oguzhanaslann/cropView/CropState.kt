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
    internal var topLeft by mutableStateOf(topLeft)
    internal var size by mutableStateOf(size)


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
        this@CropState.topLeft = topLeftInAllowedArea(topLeft, allowedWidth, allowedHeight)
        this@CropState.size = sizeInLimits(size, allowedWidth, allowedHeight)
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
        topLeft = newTopLeft

    }

    internal fun setSize(width: Float, height: Float) {
        val newSize = sizeInLimits(
            size = Size(width, height),
            allowedWidth = gridAllowedArea.width,
            allowedHeight = gridAllowedArea.height
        )
        size = newSize
    }

    internal fun setGridAllowedArea(width: Float, height: Float) {
        gridAllowedArea = Size(width, height)
    }

    inline fun widthLimitCheck(
        targetWidth: Float,
        topLeft: Offset,
        allowedWidth: Float,
        minWidth: Float,
        onGridAreaLimitExceeded: () -> Unit,
        onMinWidthLimitExceeded: () -> Unit,
        onEach: () -> Unit,
        onElse: () -> Unit = {},
    ) {

        val isWidthBiggerThanAllowedWidth = targetWidth + topLeft.x > allowedWidth
        val isWidthLessThanMinWidth = targetWidth < minWidth
        when {
            isWidthBiggerThanAllowedWidth -> {
                onGridAreaLimitExceeded()
                onEach()
            }

            isWidthLessThanMinWidth -> {
                onMinWidthLimitExceeded()
                onEach()
            }

            else -> {
                onElse()
            }
        }

    }

    fun setAspectRatio(ratio: Ratio) {
        val currentRatio = size.width / size.height
        val targetRatio = ratio.value
        if (currentRatio == targetRatio) { return }

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

            if (applyNewHeight) { break }

            appliedWidth = appliedHeight * targetRatio

            val widthBiggerThanAllowedWidth = isWidthBiggerThanAllowedWidth(appliedWidth)
            val widthLessThanMinWidth = isWidthLessThanMinWidth(targetWidth = appliedWidth)
            val applyNewWidth = !widthBiggerThanAllowedWidth && !widthLessThanMinWidth

            if (applyNewWidth) { break }

            appliedWidth = when {
                widthBiggerThanAllowedWidth -> gridAllowedArea.width - topLeft.x
                else -> minSize.width
            }

        } while (true);

        size = Size(appliedWidth, appliedHeight)
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

    @JvmInline
    value class Ratio private constructor(
        val value: Float,
    ) {
        companion object {
            val RATIO_16_9 = Ratio(16f / 9f)
            val RATIO_9_16 = Ratio(9f / 16f)
            val RATIO_4_3 = Ratio(4f / 3f)
            val RATIO_3_4 = Ratio(3f / 4f)
            val RATIO_1_1 = Ratio(1f / 1f)
        }
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
