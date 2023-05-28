package com.example.andromedia.ui.imageEdit

import androidx.compose.ui.graphics.ColorMatrix


val grayFilter = ColorFilterModel(
    name = "Gray",
    colorMatrix = ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f

        )
    ).apply { setToSaturation(0f) }
)


val yellowFilter = ColorFilterModel(
    name = "Yellow",
    colorMatrix = ColorMatrix(
        values = floatArrayOf(
            0.5f, 0f, 0f, 0f, 100f,
            0f, 0.5f, 0f, 0f, 100f,
            0f, 0f, 0.5f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)


val blueFilter = ColorFilterModel(
    name = "Blue",
    colorMatrix = ColorMatrix(
        values = floatArrayOf(
            0.5f, 0f, 0f, 0f, 0f,
            0f, 0.5f, 0f, 0f, 0f,
            0f, 0f, 1.5f, 0f, 50f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

val goldFilter = ColorFilterModel(
    name = "Gold",
    colorMatrix = ColorMatrix(
        values = floatArrayOf(
            1.2f, 0f, 0f, 0f, 50f,
            0f, 1.1f, 0f, 0f, 50f,
            0f, 0f, 0.8f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

val pinkFilter = ColorFilterModel(
    name = "Pink",
    colorMatrix = ColorMatrix(
        values = floatArrayOf(
            1.2f, 0.2f, 0.2f, 0f, 0f,
            0.1f, 1.1f, 0.1f, 0f, 0f,
            0.1f, 0.1f, 1.2f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

val greenFilter = ColorFilterModel(
    name = "Green",
    colorMatrix = ColorMatrix(
        values = floatArrayOf(
            0.9f, 0f, 0f, 0f, 0f,
            0f, 1.2f, 0f, 0f, 50f,
            0f, 0f, 0.9f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

val sepiaFilter = ColorFilterModel(
    name = "Sepia",
    colorMatrix = ColorMatrix(
        values = floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)


