package com.example.andromedia.ui.imageEdit

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.andromedia.R
import com.example.andromedia.ui.SelectImageView
import com.example.andromedia.ui.ShapeableImage
import com.example.andromedia.ui.theme.AndromediaTheme


val brightnessRange = -180f..180f
val contrastRange = 0f..10f
val blurRange = 0f..10f

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImageEditView() {

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { photoUri = it } }
    )

    var brightness by remember { mutableStateOf(0.0f) }
    var contrast by remember { mutableStateOf(0.625f) }
    var blur by remember { mutableStateOf(0f) }

    var rotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(stiffness = 100f),
    )

    var editPanel by remember { mutableStateOf<EditPanel?>(null) }
    val editPanelOpen by remember(editPanel) { derivedStateOf { editPanel != null } }

    var appliedFilter: ColorFilterModel? by remember { mutableStateOf(null) }

    BackHandler(editPanelOpen) {
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
                        blur = blur,
                        rotation = animatedRotation,
                        colorMatrix = appliedFilter?.colorMatrix,
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

                    IconButton(onClick = {
                        rotation = (rotation - 90f) % (Float.MAX_VALUE - 90f)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_rotate_90_degrees_ccw_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = {
                        editPanel = if (editPanel == EditPanel.ADJUST) null else EditPanel.ADJUST
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_wb_sunny_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }


                    IconButton(onClick = {
                        editPanel = if (editPanel == EditPanel.FILTER) null else EditPanel.FILTER
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
                AnimatedContent(
                    modifier = Modifier.heightIn(96.dp),
                    targetState = editPanel,
                ) { panel ->
                    when (editPanel) {
                        EditPanel.ADJUST -> AdjustImageSettingsView(
                            brightness = brightness,
                            onBrightnessChange = { brightness = it },
                            contrast = contrast,
                            onContrastChange = { contrast = it },
                            blur = blur,
                            onBlurChange = { blur = it }
                        )
                        EditPanel.FILTER -> Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AdjustedImageView(photoUri!!) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, grayFilter) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, yellowFilter) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, blueFilter) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, goldFilter) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, pinkFilter) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, greenFilter) { appliedFilter = it }
                            AdjustedImageView(photoUri!!, sepiaFilter) { appliedFilter = it }
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustImageSettingsView(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    contrast: Float,
    onContrastChange: (Float) -> Unit,
    blur: Float,
    onBlurChange: (Float) -> Unit,
) {
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
            onValueChange = onBrightnessChange,
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
            onValueChange = onContrastChange,
            modifier = Modifier.padding(horizontal = 16.dp),
            valueRange = contrastRange
        )
        Row {
            Text("Blur")
            Spacer(modifier = Modifier.weight(1f))
            Text("${(blur / (blurRange.endInclusive - blurRange.start) * 100f).toInt()}%")
        }
        Slider(
            value = blur,
            onValueChange = onBlurChange,
            modifier = Modifier.padding(horizontal = 16.dp),
            valueRange = blurRange
        )
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
    rotation: Float = 0f,
    colorMatrix: ColorMatrix? = null,
) {

    val updatedColorMatrix = remember(
        contrast,
        brightness,
        colorMatrix
    ) {
        // Create a new matrix or clone the provided one if not null
        val matrix = colorMatrix?.values?.clone() ?: floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        )
        // Apply the brightness value
        matrix.apply {
            set(4, get(4) + brightness)
            set(9, get(9) + brightness)
            set(14, get(14) + brightness)
        }
        // Apply the contrast value
        matrix.apply {
            val scale = contrast + 1f
            set(0, get(0) * scale)
            set(6, get(6) * scale)
            set(12, get(12) * scale)
            set(18, get(18) * scale)
        }
        ColorMatrix(matrix)
    }


    LaunchedEffect(key1 = blur.dp, block = { Log.e("TAG", "ImageOnEditView: $blur") })

    AsyncImage(
        modifier = modifier
            .rotate(rotation)
            .blur(blur.dp),
        model = uri,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        colorFilter = ColorFilter.colorMatrix(
            updatedColorMatrix
        ),
        onState = {
            if (it is AsyncImagePainter.State.Success) {
                it.result.drawable.toBitmapOrNull()
            }
        }
    )
}



@Preview
@Composable
fun PreviewImageEditView() {
    AndromediaTheme {
        ImageEditView()
    }
}


@Composable
fun AdjustedImageView(
    imageUri: Uri? = null,
    colorFilterModel: ColorFilterModel? = null,
    onClick: (ColorFilterModel?) -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ShapeableImage(
            modifier = Modifier
                .size(96.dp)
                .clickable(onClick = { onClick(colorFilterModel) }),
            shape = RoundedCornerShape(16.dp),
            imageUrl = imageUri.toString(),
            colorFilter = colorFilterModel?.colorFilter,
        )

        Text(
            text = colorFilterModel?.name ?: "None",
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun previewAdjustedImageView() {
    AndromediaTheme {
        AdjustedImageView()
    }
}