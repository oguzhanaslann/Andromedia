package com.oguzhanaslann.cropView

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class CropState(
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
) {
    internal var topLeft by mutableStateOf(topLeft)
    internal var size by mutableStateOf(size)

    fun setAspectRatio(ratio1619: Ratio) {
        size = Size(
            width = size.height * ratio1619.ratio,
            height = size.height,
        )
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
) = remember(size) {
    CropState(
        topLeft = Offset.Zero,
        size = size,
    )
}
