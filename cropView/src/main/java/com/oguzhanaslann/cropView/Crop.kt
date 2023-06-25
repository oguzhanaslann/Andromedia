package com.oguzhanaslann.cropView

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Crop(
    modifier: Modifier = Modifier,
    gridCropState: GridCropState = rememberGridCropState(),
    drawGrid: Boolean = true,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthPx = maxWidth.toPx()
        val maxHeightPx = maxHeight.toPx()

        LaunchedEffect(maxWidthPx, maxHeightPx) {
            when {
                gridCropState.size == Size.Zero -> {
                    gridCropState.setSize(maxWidthPx, maxHeightPx)
                }

                gridCropState.size.width > maxWidthPx || gridCropState.size.height > maxHeightPx -> {
                    gridCropState.setSize(maxWidthPx, maxHeightPx)
                }
            }
        }

        content()
        AnimatedVisibility(
            visible = drawGrid,
            modifier = Modifier
                .size(
                    width = maxWidth,
                    height = maxHeight
                )
                .align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GridView(
                gridCropState,
                maxSize = Size(
                    width = maxWidthPx,
                    height = maxHeightPx
                ),
                onDrawGrid = {
                    gridCropState.setGridAllowedArea(
                        width = size.width,
                        height = size.height,
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun previewCrop() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Crop {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                color = Color.Gray,
                content = {}
            )
        }
    }
}
