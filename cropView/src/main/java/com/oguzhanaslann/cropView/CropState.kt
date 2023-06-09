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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CropState(
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    coroutineScope: CoroutineScope,
    val minSize : Size,
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

    fun setAspectRatio(ratio: Ratio) {
        val allowedArea = gridAllowedArea
        val minSize = minSize
        val currentSize = size
        var currentHeight = currentSize.width
        var currentWidth = currentSize.height
        val nextHeight = currentSize.width / ratio.ratio
        val isHeightBiggerThanAllowedHeight = nextHeight + topLeft.y > allowedArea.height
        val isWidthBiggerThanAllowedWidth = currentSize.height * ratio.ratio + topLeft.x > allowedArea.width

        when {
            isHeightBiggerThanAllowedHeight -> {
                currentHeight = allowedArea.height - topLeft.y
                currentWidth = currentHeight * ratio.ratio
            }

            isWidthBiggerThanAllowedWidth -> {
                currentWidth = allowedArea.width - topLeft.x
                currentHeight = currentWidth / ratio.ratio
            }

            else -> {
                currentWidth = nextHeight
                currentHeight = currentSize.width
            }
        }


        size = Size(currentWidth, currentHeight)


//        val currentRatio = size.width / size.height
//        size = if (currentRatio > ratio.ratio) {
//            val newWidth = size.height * ratio.ratio
//            Size(newWidth, size.height)
//        } else {
//            val newHeight = size.width / ratio.ratio
//            Size(size.width, newHeight)
//        }

    }


    @JvmInline
    value class Ratio private constructor(val ratio: Float) {
        companion object {
            val RATIO_16_9 = Ratio(16f / 9f)
            val RATIO_9_16 = Ratio(9f / 16f)
            val RATIO_4_3 = Ratio(4f / 3f)
            val RATIO_3_4 = Ratio(3f / 4f)
            val RATIO_1_1 = Ratio(1f)
        }
    }
}

@Composable
fun rememberCropState(
    size: Size = Size.Zero,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember(size) {
    CropState(
        topLeft = Offset.Zero,
        size = size,
        coroutineScope = coroutineScope,
        minSize = Size(100f, 100f)
    )
}
