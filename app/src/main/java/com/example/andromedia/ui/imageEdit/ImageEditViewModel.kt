package com.example.andromedia.ui.imageEdit

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oguzhanaslann.cropView.util.blur
import com.oguzhanaslann.cropView.util.cropped
import com.oguzhanaslann.cropView.util.filtered
import com.oguzhanaslann.cropView.util.rotate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditState(
    val brightness: Float,
    val contrast: Float,
    val blur: Float,
    val rotation: Float,
    val filter: ColorFilterModel? = null,
)

class ImageEditViewModel : ViewModel() {
    private val original = MutableStateFlow<Bitmap?>(null)
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> get() = _bitmap


    val brightness = MutableStateFlow(0.0f)
    val contrast = MutableStateFlow(0.625f)
    val blur = MutableStateFlow(0.0f)
    val rotation = MutableStateFlow(0.0f)
    val filter = MutableStateFlow<ColorFilterModel?>(noneFilter)

    val state = combine(
        brightness,
        contrast,
        blur,
        rotation,
        filter
    ) { brightness, contrast, blur, rotation, filter ->
        EditState(
            brightness,
            contrast,
            blur,
            rotation,
            filter = filter
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, EditState(0f, 0f, 0f, 0f))


    fun onImageLoaded(bitmap: Bitmap?) {
        viewModelScope.launch {
            original.value = bitmap
        }
    }

    fun applyChanges(
        cropTopLeft: Pair<Float, Float>,
        cropSize: Pair<Float, Float>,
    ) {
        val colorMatrix = filter.value?.colorMatrix ?: kotlin.run {
            _bitmap.value = original.value
            return
        }
        val bitmap = original.value ?: return
        _bitmap.value = bitmap
            .copy(Bitmap.Config.ARGB_8888, true)
            .cropped(cropTopLeft.first, cropTopLeft.second, cropSize.first, cropSize.second)
            .rotate(rotation.value)
            .blur(blur.value * 1.5f)
            .filtered(colorMatrix, brightness.value, contrast.value)
    }

    fun setRotation(fl: Float) {
        rotation.value = fl
    }

    fun setBrightness(it: Float) {
        brightness.value = it
    }

    fun setContrast(it: Float) {
        contrast.value = it
    }

    fun setBlur(it: Float) {
        blur.value = it
    }

    fun setFilter(filter: ColorFilterModel?) {
        this.filter.value = filter
    }
}
