package com.oguzhanaslann.cropView

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx(): Float {
    val density = Density(LocalContext.current)
    return with(density) { toPx() }
}