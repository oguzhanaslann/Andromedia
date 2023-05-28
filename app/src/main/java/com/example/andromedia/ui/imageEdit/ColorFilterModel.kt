package com.example.andromedia.ui.imageEdit

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

data class ColorFilterModel(
    val name: String,
    val colorMatrix: ColorMatrix,
) {

    val colorFilter: ColorFilter
        get() = ColorFilter.colorMatrix(colorMatrix)
}