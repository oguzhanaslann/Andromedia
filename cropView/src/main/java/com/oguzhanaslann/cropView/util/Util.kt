package com.oguzhanaslann.cropView.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size


internal fun boundedNewX(
    topLeft: Offset,
    dragAmount: Offset,
    size: Size,
    maxWidthPx: Float,
) = when {
    topLeft.x + dragAmount.x < 0 -> 0f
    topLeft.x + size.width + dragAmount.x > maxWidthPx -> maxWidthPx - size.width
    else -> topLeft.x + dragAmount.x
}

internal  fun boundedNewY(
    topLeft: Offset,
    dragAmount: Offset,
    size: Size,
    maxHeightPx: Float,
) = when {
    topLeft.y + dragAmount.y < 0 -> 0f
    topLeft.y + size.height + dragAmount.y > maxHeightPx -> maxHeightPx - size.height
    else -> topLeft.y + dragAmount.y
}

internal  fun boundedWidth(
    newWidth: Float,
    maxWidthPx: Float,
) = when {
    newWidth < 0 -> 0f
    newWidth > maxWidthPx -> maxWidthPx
    else -> newWidth
}

internal fun boundedHeight(
    newHeight: Float,
    maxHeightPx: Float,
) = when {
    newHeight < 0 -> 0f
    newHeight > maxHeightPx -> maxHeightPx
    else -> newHeight
}

internal fun isWidthBiggerThanAllowedWidth(
    targetWidth: Float,
    allowedWidth: Float,
    leftX : Float
): Boolean {
    return targetWidth + leftX > allowedWidth
}

internal fun isWidthLessThanMinWidth(
    targetWidth: Float,
    minWidth: Float
): Boolean {
    return targetWidth < minWidth
}

internal fun isHeightBiggerThanAllowedHeight(
    targetHeight: Float,
    allowedHeight: Float,
    topY : Float
): Boolean {
    return targetHeight + topY > allowedHeight
}

internal fun isHeightLessThanMinHeight(
    targetHeight: Float,
    minHeight: Float
): Boolean {
    return targetHeight < minHeight
}