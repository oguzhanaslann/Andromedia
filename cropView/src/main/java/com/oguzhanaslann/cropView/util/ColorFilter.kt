package com.oguzhanaslann.cropView.util

import androidx.compose.ui.graphics.ColorMatrix

val gray = ColorMatrix(
    floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
).apply { setToSaturation(0f) }


val yellow = ColorMatrix(
    values = floatArrayOf(
        0.5f, 0f, 0f, 0f, 100f,
        0f, 0.5f, 0f, 0f, 100f,
        0f, 0f, 0.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)

val blue = ColorMatrix(
    values = floatArrayOf(
        0.5f, 0f, 0f, 0f, 0f,
        0f, 0.5f, 0f, 0f, 0f,
        0f, 0f, 1.5f, 0f, 50f,
        0f, 0f, 0f, 1f, 0f
    )
)

val gold = ColorMatrix(
    values = floatArrayOf(
        1.2f, 0f, 0f, 0f, 50f,
        0f, 1.1f, 0f, 0f, 50f,
        0f, 0f, 0.8f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)


val pink = ColorMatrix(
    values = floatArrayOf(
        1.2f, 0.2f, 0.2f, 0f, 0f,
        0.1f, 1.1f, 0.1f, 0f, 0f,
        0.1f, 0.1f, 1.2f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)


val green = ColorMatrix(
    values = floatArrayOf(
        0.9f, 0f, 0f, 0f, 0f,
        0f, 1.2f, 0f, 0f, 50f,
        0f, 0f, 0.9f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)

val sepia = ColorMatrix(
    values = floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)


val none = ColorMatrix().apply {
    setToSaturation(0.8f)
    setToScale(0.8f, 0.8f, 0.8f, 1f)
}

fun ColorMatrix?.brightnessApplied(brightness: Float): ColorMatrix {
    val matrix = this?.values?.clone() ?: ColorMatrix().values
    matrix.apply {
        set(4, get(4) + brightness)
        set(9, get(9) + brightness)
        set(14, get(14) + brightness)
    }
    return ColorMatrix(matrix)
}

fun ColorMatrix?.contractionApplied(contraction: Float): ColorMatrix {
    val matrix = this?.values?.clone() ?: ColorMatrix().values
    matrix.apply {
        val scale = contraction + 1f
        set(0, get(0) * scale)
        set(6, get(6) * scale)
        set(12, get(12) * scale)
        set(18, get(18) * scale)
    }
    return ColorMatrix(matrix)
}


