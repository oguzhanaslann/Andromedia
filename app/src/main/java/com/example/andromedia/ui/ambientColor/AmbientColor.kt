package com.example.andromedia.ui.ambientColor

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.example.andromedia.ui.ShapeableImage
import com.example.andromedia.ui.theme.AndromediaTheme

@Composable
fun AmbientColorView(
    modifier : Modifier = Modifier
) {

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var drawable by remember { mutableStateOf<Drawable?>(null) }

    val photoPicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> uri?.let { photoUri = it } })


    var ambientColor by remember { mutableStateOf(Color.White) }

    LaunchedEffect(drawable) {
        val drawable = drawable ?: return@LaunchedEffect
        val hsl = getHslOfDrawable(drawable)
        hsl?.let {
            ambientColor = Color.hsl(hsl.hue(), 0.9f, 0.06f, 1f)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ambientColor
    ) {
        Box {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShapeableImage(
                    modifier = Modifier
                        .height(128.dp)
                        .width(128.dp),
                    imageUri = photoUri,
                    drawableCallback = { drawable = it },
                )
                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Text(text = "Select Image")
                }
            }
        }
    }
}

private fun getHslOfDrawable(drawable: Drawable): FloatArray? {
    val palette = Palette.Builder(
        drawable.toBitmap()
            .copy(Bitmap.Config.RGBA_F16, true)
    )
        .clearFilters()
        .maximumColorCount(8)
        .generate()

    val hsl = palette.dominantSwatch?.hsl
    return hsl
}

fun FloatArray.hue(): Float {
    return this[0]
}
@Preview
@Composable
fun previewAmbient() {
    AndromediaTheme {
        AmbientColorView()
    }
}