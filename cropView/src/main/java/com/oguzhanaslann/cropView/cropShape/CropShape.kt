package com.oguzhanaslann.cropView.cropShape

import androidx.compose.runtime.Composable
import com.oguzhanaslann.cropView.cropShape.cropState.CropState

interface CropShape {
    val state: CropState

    @Composable
    fun content(maxWidthPx: Float, maxHeightPx: Float)
}