package com.example.andromedia.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.andromedia.R
import com.example.andromedia.ui.theme.AndromediaTheme


val brightnessRange = -180f..180f
val contrastRange = 0f..10f
val blurRange = 0f..10f

@Composable
fun ImageEditView() {

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var editPanel: EditPanel? by remember { mutableStateOf(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { photoUri = it } }
    )


    var brightness by remember { mutableStateOf(0.0f) }
    var contrast by remember { mutableStateOf(1f) }
    var blur by remember { mutableStateOf(0f) }

    LaunchedEffect(brightness, contrast, blur) {
        Log.e(
            "TAG",
            "ImageEditView: brightness: $brightness, contrast: $contrast, blur: $blur"
        )
    }


    BackHandler(editPanel != null) {
        editPanel = null
    }


    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 48.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                val onBackPressedDispatcher =
                    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                when (photoUri) {
                    null -> SelectImageView(
                        modifier = Modifier.fillMaxSize(),
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                    else -> ImageOnEditView(
                        modifier = Modifier.fillMaxSize(),
                        uri = photoUri!!,
                        brightness = brightness,
                        contrast = contrast,
                        blur = blur
                    )
                }
            }

            AnimatedVisibility(visible = photoUri != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_crop_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_rotate_90_degrees_ccw_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = {
                        editPanel = if (editPanel != EditPanel.FILTER) EditPanel.FILTER else null
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_wb_sunny_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }


                    IconButton(onClick = {
                        editPanel = if (editPanel != EditPanel.ADJUST) EditPanel.ADJUST else null
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_auto_fix_normal_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }


            AnimatedVisibility(visible = editPanel != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Row {
                        Text("Brightness")
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${(brightness / 180f * 100f).toInt()}%")
                    }
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        valueRange = brightnessRange
                    )
                    Row {
                        Text("Contrast")
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${(contrast / 10f * 100f).toInt()}%")
                    }
                    Slider(
                        value = contrast,
                        onValueChange = { contrast = it },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        valueRange = contrastRange
                    )
                    Row {
                        Text("Blur")
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${(blur / 25f * 100f).toInt()  }%")
                    }
                    Slider(
                        value = blur,
                        onValueChange = { blur = it },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        valueRange = blurRange
                    )
                }
            }
        }
    }
}

enum class EditPanel {
    FILTER, ADJUST
}

@Composable
fun ImageOnEditView(
    modifier: Modifier = Modifier,
    uri: Uri,
    brightness: Float = 0f,
    contrast: Float = 0f,
    blur: Float = 0f,
) {
    val colorMatrix = floatArrayOf(
        contrast, 0f, 0f, 0f, brightness,
        0f, contrast, 0f, 0f, brightness,
        0f, 0f, contrast, 0f, brightness,
        0f, 0f, 0f, 1f, 0f
    )


    AsyncImage(
        modifier = modifier
            .blur(
                radiusX = blur.dp,
                radiusY = blur.dp
            ),
        model = uri,
        contentScale = ContentScale.Inside,
        contentDescription = null,
        colorFilter = ColorFilter.colorMatrix(
            ColorMatrix(colorMatrix)
        )
    )
}

@Composable
private fun SelectImageView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        border = BorderStroke(2.dp, Color.LightGray),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription = null,
                )
                Text(
                    text = "No Image Selected",
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewImageEditView() {
    AndromediaTheme {
        ImageEditView()
    }
}