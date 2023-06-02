package com.example.andromedia.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.graphics.drawable.toBitmap
import coil.compose.SubcomposeAsyncImage


@Composable
fun ShapeableImage(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    shape: Shape = RectangleShape,
    placeHolderColor: Color = Color.LightGray,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = if (painter == null) placeHolderColor else placeHolderColor.copy(0f)
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
                colorFilter = colorFilter
            )
        }
    }
}

@Composable
fun ShapeableImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    shape: Shape = RectangleShape,
    placeHolderColor: Color = Color.LightGray,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null,
    loadingContent: @Composable () -> Unit = { EmptyImageSurface(modifier, shape, placeHolderColor) },
    errorContent: @Composable () -> Unit = { EmptyImageSurface(modifier, shape, placeHolderColor) }
) {

    PreviewDispatchedView(
        mainContent = {
            when {
                imageUrl.isNotEmpty() -> SubcomposeAsyncImage(
                    modifier = modifier,
                    model = imageUrl,
                    contentDescription = contentDescription,
                    loading = { loadingContent() } ,
                    error = { errorContent() },
                    success = {
                        ShapeableImage(
                            modifier = modifier,
                            painter = painter,
                            shape = shape,
                            placeHolderColor = placeHolderColor,
                            contentScale = contentScale,
                            contentDescription = contentDescription,
                            colorFilter = colorFilter
                        )
                    },
                    contentScale = contentScale
                )
                else -> EmptyImageSurface(modifier, shape, placeHolderColor)
            }
        },
        previewContent = { EmptyImageSurface(modifier, shape, placeHolderColor) }
    )
}

@Composable
fun ShapeableImage(
    modifier: Modifier = Modifier,
    imageUri : Uri? = null,
    shape: Shape = RectangleShape,
    placeHolderColor: Color = Color.LightGray,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null,
    loadingContent: @Composable () -> Unit = { EmptyImageSurface(modifier, shape, placeHolderColor) },
    errorContent: @Composable () -> Unit = { EmptyImageSurface(modifier, shape, placeHolderColor) },
    drawableCallback : (drawable : android.graphics.drawable.Drawable) -> Unit = {}
) {

    PreviewDispatchedView(
        mainContent = {
            when {
                imageUri != null -> SubcomposeAsyncImage(
                    modifier = modifier,
                    model = imageUri,
                    contentDescription = contentDescription,
                    loading = { loadingContent() } ,
                    error = { errorContent() },
                    success = {
                        SideEffect {
                            drawableCallback(it.result.drawable)
                        }
                        ShapeableImage(
                            modifier = modifier,
                            painter = painter,
                            shape = shape,
                            placeHolderColor = placeHolderColor,
                            contentScale = contentScale,
                            contentDescription = contentDescription,
                            colorFilter = colorFilter
                        )
                    },
                    contentScale = contentScale
                )
                else -> EmptyImageSurface(modifier, shape, placeHolderColor)
            }
        },
        previewContent = { EmptyImageSurface(modifier, shape, placeHolderColor) }
    )
}

@Composable
private fun EmptyImageSurface(
    modifier: Modifier,
    shape: Shape,
    placeHolderColor: Color
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = placeHolderColor,
        content = {}
    )
}


@Composable
fun PreviewDispatchedView(
    mainContent: @Composable () -> Unit,
    previewContent: @Composable () -> Unit
) {
    if (LocalInspectionMode.current) {
        previewContent()
    } else {
        mainContent()
    }
}