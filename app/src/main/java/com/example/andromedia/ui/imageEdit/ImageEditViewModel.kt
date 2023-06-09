package com.example.andromedia.ui.imageEdit

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ImageEditViewModel : ViewModel() {
    private val original = MutableStateFlow<Bitmap?>(null)
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> get() = _bitmap

    fun onImageLoaded(bitmap: Bitmap?) {
        viewModelScope.launch {
            original.value = bitmap
        }
    }

    fun applyChanges(
        colorMatrix: ColorMatrix? = null,
        brightness: Float? = null,
        contrast: Float? = null,
        rotation: Float,
        cropTopLeft: Pair<Float, Float>,
        cropSize: Pair<Float, Float>,
    ) {
        val colorMatrix = colorMatrix ?: kotlin.run {
            _bitmap.value = original.value
            return
        }
        val bitmap = original.value ?: return
        val targetBmp: Bitmap = bitmap
            .copy(Bitmap.Config.ARGB_8888, false)
            .cropped(
                cropTopLeft.first,
                cropTopLeft.second,
                cropSize.first,
                cropSize.second
            )
            .rotate(rotation)

        val resultBitmap =
            Bitmap.createBitmap(targetBmp.width, targetBmp.height, Bitmap.Config.ARGB_8888)

        // Create a Canvas object to draw the modified image
        val canvas = Canvas(resultBitmap)

        // Create a Paint object with ColorFilter to apply the color matrix
        val matrix = colorMatrix.values.clone()
        brightness?.let {
            matrix.apply {
                set(4, get(4) + brightness)
                set(9, get(9) + brightness)
                set(14, get(14) + brightness)
            }
        }

        contrast?.let {
            matrix.apply {
                val scale = contrast + 1f
                set(0, get(0) * scale)
                set(6, get(6) * scale)
                set(12, get(12) * scale)
                set(18, get(18) * scale)
            }
        }

        val updatedMatrix = ColorMatrix(matrix)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(updatedMatrix.values)
        }


        // Draw the original bitmap on the canvas with the color filter applied
        canvas.drawBitmap(targetBmp, 0f, 0f, paint)

        // Update the reference to the modified bitmap
        _bitmap.value = resultBitmap

    }
}

fun Bitmap.rotate(angle: Float): Bitmap {
    val matrix = android.graphics.Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.cropped(topLeftX: Float, topLeftY: Float, width: Float, height: Float): Bitmap {
    return Bitmap.createBitmap(
        this,
        topLeftX.toInt(),
        topLeftY.toInt(),
        width.toInt(),
        height.toInt()
    )
}

fun Bitmap.blur(radius: Float) {

}


private const val RED_MASK = 0xff0000
private const val RED_MASK_SHIFT = 16
private const val GREEN_MASK = 0x00ff00
private const val GREEN_MASK_SHIFT = 8
private const val BLUE_MASK = 0x0000ff

fun gaussianBlur(source: Bitmap): Bitmap? {
    val width = source.width
    val height = source.height
    val numPixels = width * height
    val `in` = IntArray(numPixels)
    val tmp = IntArray(numPixels)
    source.getPixels(`in`, 0, width, 0, 0, width, height)
    gaussianBlurFilter(`in`, tmp, width, height)
    gaussianBlurFilter(tmp, `in`, width, height)
    // Return a bitmap scaled to the desired size.
    val filtered = Bitmap.createBitmap(`in`, width, height, Bitmap.Config.ARGB_8888)
    source.recycle()
    return filtered
}

private fun gaussianBlurFilter(`in`: IntArray, out: IntArray, width: Int, height: Int) {
    // This function is currently hardcoded to blur with RADIUS = 4.
    // (If you change RADIUS, you'll have to change the weights[] too.)
    val RADIUS = 4
    val weights = intArrayOf(13, 23, 32, 39, 42, 39, 32, 23, 13) // Adds up to 256
    var inPos = 0
    val widthMask = width - 1 // width must be a power of two.
    for (y in 0 until height) {
        // Compute the alpha value.
        val alpha = 0xff
        // Compute output values for the row.
        var outPos = y
        for (x in 0 until width) {
            var red = 0
            var green = 0
            var blue = 0
            for (i in -RADIUS..RADIUS) {
                val argb = `in`[inPos + (widthMask and x + i)]
                val weight = weights[i + RADIUS]
                red += weight * (argb and RED_MASK shr RED_MASK_SHIFT)
                green += weight * (argb and GREEN_MASK shr GREEN_MASK_SHIFT)
                blue += weight * (argb and BLUE_MASK)
            }
            // Output the current pixel.
            out[outPos] = (alpha shl 24 or (red shr 8 shl RED_MASK_SHIFT)
                    or (green shr 8 shl GREEN_MASK_SHIFT)
                    or (blue shr 8))
            outPos += height
        }
        inPos += width
    }
}
