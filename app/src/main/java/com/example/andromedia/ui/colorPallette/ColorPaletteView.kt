package com.example.andromedia.ui.colorPallette

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.example.andromedia.ui.ImageSelectDispatchedView
import com.example.andromedia.ui.ShapeableImage
import com.example.andromedia.ui.theme.AndromediaTheme

@Composable
fun ColorPaletteView() {
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var drawable by remember { mutableStateOf<Drawable?>(null) }

    val photoPicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> uri?.let { photoUri = it } })


    var dominantSwatch by remember { mutableStateOf<Int?>(null) }
    var mostDominantSwatch by remember { mutableStateOf<Int?>(null) }
    var mutedSwatch by remember { mutableStateOf<Int?>(null) }
    var darkMutedSwatch by remember { mutableStateOf<Int?>(null) }
    var lightMutedSwatch by remember { mutableStateOf<Int?>(null) }
    var darkVibrantSwatch by remember { mutableStateOf<Int?>(null) }
    var lightVibrantSwatch by remember { mutableStateOf<Int?>(null) }
    var vibrantSwatch by remember { mutableStateOf<Int?>(null) }


    LaunchedEffect(drawable) {
        val drawable = drawable ?: return@LaunchedEffect
        val palette = Palette.Builder(
            drawable.toBitmap()
                .copy(Bitmap.Config.RGBA_F16, true)
        )
            .clearFilters()
            .maximumColorCount(8)
            .generate()

        palette.swatches
            .sortedBy { it.population }
            .firstOrNull()
            ?.let { swatch ->
                Log.e("TAG", "ColorPaletteView: ${swatch.rgb}")
                mostDominantSwatch = swatch.rgb
            }

        dominantSwatch = palette.dominantSwatch?.rgb
        mutedSwatch = palette.mutedSwatch?.rgb
        darkMutedSwatch = palette.darkMutedSwatch?.rgb
        lightMutedSwatch = palette.lightMutedSwatch?.rgb
        darkVibrantSwatch = palette.darkVibrantSwatch?.rgb
        lightVibrantSwatch = palette.lightVibrantSwatch?.rgb
        vibrantSwatch = palette.vibrantSwatch?.rgb
    }

    val material3Colors = MaterialTheme.colorScheme
        .run {
            dominantSwatch?.let { copy(primary = Color(it)) } ?: this
        }.run {
            mutedSwatch?.let { copy(secondary = Color(it)) } ?: this
        }.run {
            darkMutedSwatch?.let { copy(error = Color(it)) } ?: this
        }.run {
            lightMutedSwatch?.let { copy(surface = Color(it)) } ?: this
        }

    MaterialTheme(colorScheme = material3Colors) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val halfHeight = maxHeight / 2
                val surfaceColor =
                    mostDominantSwatch?.let { Color(it) } ?: MaterialTheme.colorScheme.background
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(halfHeight + 32.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    surfaceColor,
                                    surfaceColor.copy(alpha = 0.5f),
                                    surfaceColor.copy(alpha = 0.0f)
                                )
                            )
                        )
                    ,
                    color = Color.Transparent,
                    content = {}
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImageSelectDispatchedView(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp)
                            .heightIn(max = halfHeight),
                        image = photoUri,
                        imageContent = { modifier, uri ->
                            ShapeableImage(
                                modifier = modifier.heightIn(max = halfHeight),
                                imageUri = uri,
                                drawableCallback = { drawable = it },
                            )
                        })

                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }) {
                            Text(text = "Select Image")
                        }

                        OutlinedButton(onClick = { photoUri = null }) {
                            Text(text = "Remove Image")
                        }
                    }

                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ColorShowcaseView(
                            color = Color(dominantSwatch ?: 0),
                            description = "Dominant"
                        )
                        ColorShowcaseView(color = Color(mutedSwatch ?: 0), description = "Muted")
                        ColorShowcaseView(
                            color = Color(darkMutedSwatch ?: 0),
                            description = "Dark Muted"
                        )
                        ColorShowcaseView(
                            color = Color(lightMutedSwatch ?: 0),
                            description = "Light Muted"
                        )
                        ColorShowcaseView(
                            color = Color(darkVibrantSwatch ?: 0),
                            description = "Dark Vibrant"
                        )
                        ColorShowcaseView(
                            color = Color(lightVibrantSwatch ?: 0),
                            description = "Light Vibrant"
                        )
                        ColorShowcaseView(
                            color = Color(vibrantSwatch ?: 0),
                            description = "Vibrant"
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun previewColorPaletteView() {
    AndromediaTheme {
        ColorPaletteView()
    }
}