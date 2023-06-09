package com.oguzhanaslann.cropView

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun PointerInputScope.isTopLeftCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
): Boolean {
    return xDp in topLeft.x.toDp() - 48.dp..topLeft.x.toDp() + 48.dp
            && yDp in topLeft.y.toDp() - 48.dp..topLeft.y.toDp() + 48.dp
}

internal fun PointerInputScope.isTopRightCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + size.width.toDp() - 48.dp..topLeft.x.toDp() + size.width.toDp() + 48.dp
            && yDp in topLeft.y.toDp() - 48.dp..topLeft.y.toDp() + 48.dp
}

internal fun PointerInputScope.isBottomLeftCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() - 48.dp..topLeft.x.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + size.height.toDp() - 48.dp..topLeft.y.toDp() + size.height.toDp() + 48.dp
}

internal fun PointerInputScope.isBottomRightCorner(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + size.width.toDp() - 48.dp..topLeft.x.toDp() + size.width.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + size.height.toDp() - 48.dp..topLeft.y.toDp() + size.height.toDp() + 48.dp
}

internal fun PointerInputScope.isMiddle(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + 48.dp..topLeft.x.toDp() + size.width.toDp() - 48.dp
            && yDp in topLeft.y.toDp() + 48.dp..topLeft.y.toDp() + size.height.toDp() - 48.dp
}

internal fun PointerInputScope.isLeftEdge(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() - 48.dp..topLeft.x.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + 48.dp..topLeft.y.toDp() + size.height.toDp() - 48.dp
}

internal fun PointerInputScope.isRightEdge(
    xDp: Dp,
    yDp: Dp,
    topLeft: Offset,
    size: Size,
): Boolean {
    return xDp in topLeft.x.toDp() + size.width.toDp() - 48.dp..topLeft.x.toDp() + size.width.toDp() + 48.dp
            && yDp in topLeft.y.toDp() + 48.dp..topLeft.y.toDp() + size.height.toDp() - 48.dp
}