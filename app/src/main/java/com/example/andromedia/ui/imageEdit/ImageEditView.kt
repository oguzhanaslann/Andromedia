package com.example.andromedia.ui.imageEdit

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.geometry.Size
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.andromedia.R
import com.example.andromedia.ui.SelectImageView
import com.example.andromedia.ui.ShapeableImage
import com.example.andromedia.ui.theme.AndromediaTheme
import com.oguzhanaslann.cropView.Crop
import com.oguzhanaslann.cropView.Ratio
import com.oguzhanaslann.cropView.cropShape.CropShape
import com.oguzhanaslann.cropView.cropShape.grid.rememberGridCrop
import com.oguzhanaslann.cropView.cropShape.grid.rememberGridCropState
import com.oguzhanaslann.cropView.toPx
import com.oguzhanaslann.cropView.util.brightnessApplied
import com.oguzhanaslann.cropView.util.contractionApplied

val brightnessRange = -180f..180f
val contrastRange = 0f..10f
val blurRange = 0f..10f

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImageEditView(
    imageEditViewModel: ImageEditViewModel = viewModel(),
) {

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { photoUri = it } }
    )

    val state by imageEditViewModel.state.collectAsState()
    val brightness = state.brightness
    val appliedFilter: ColorFilterModel? = state.filter
    val contrast = state.contrast
    val blur = state.blur
    val rotation = state.rotation
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(stiffness = 100f),
    )

    var editPanel by remember { mutableStateOf<EditPanel?>(null) }
    val editPanelOpen by remember(editPanel) { derivedStateOf { editPanel != null } }


    val cropState = rememberGridCrop(
        rememberGridCropState(
            size = Size(200.dp.toPx(), 200.dp.toPx()),
        )
    )

    val bitmap by imageEditViewModel.bitmap.collectAsState()

    BackHandler(editPanelOpen) {
        editPanel = null
    }

    Box {
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

                    BackButton()

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    ShareButton()

                    DoneButton(
                        imageEditViewModel,
                        cropState
                    )

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
                        .padding(bottom = 16.dp)
                ) {
                    when (photoUri) {
                        null -> SelectImageView(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surface
                                ),
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
                            imageEditViewModel = imageEditViewModel,
                            colorMatrix = appliedFilter?.colorMatrix,
                            rotation = animatedRotation,
                            uri = photoUri!!,
                            crop = editPanel == EditPanel.CROP,
                            cropShape = cropState
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
                        IconButton(onClick = {
                            editPanel = if (editPanel == EditPanel.CROP) null else EditPanel.CROP
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_crop_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(onClick = {
                            imageEditViewModel.setRotation((rotation - 90f) % (Float.MAX_VALUE - 90f))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_rotate_90_degrees_ccw_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(onClick = {
                            editPanel =
                                if (editPanel == EditPanel.ADJUST) null else EditPanel.ADJUST
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_wb_sunny_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }


                        IconButton(onClick = {
                            editPanel =
                                if (editPanel == EditPanel.FILTER) null else EditPanel.FILTER
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
                                onBrightnessChange = imageEditViewModel::setBrightness,
                                contrast = contrast,
                                onContrastChange = imageEditViewModel::setContrast,
                                blur = blur,
                                onBlurChange = imageEditViewModel::setBlur
                            )

                            EditPanel.FILTER -> LazyRow(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                state = rememberLazyListState()
                            ) {
                                items(
                                    listOf(
                                        noneFilter,
                                        grayFilter,
                                        yellowFilter,
                                        blueFilter,
                                        goldFilter,
                                        pinkFilter,
                                        greenFilter,
                                        sepiaFilter
                                    )
                                ) {
                                    AdjustedImageView(photoUri!!, it, imageEditViewModel::setFilter)
                                }
                            }

                            EditPanel.CROP -> {
                                Row(
                                    Modifier
                                        .horizontalScroll(rememberScrollState())
                                        .padding(start = 16.dp),
                                ) {
                                    Column(
                                        Modifier
                                            .width(96.dp)
                                            .clickable {
                                                cropState.setAspectRatio(Ratio.RATIO_16_9)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .width(48.dp)
                                                .aspectRatio(16f / 9f),
                                            color = Color.Gray,
                                            content = {}
                                        )
                                        Text(text = "16:9")
                                    }

                                    Column(
                                        Modifier
                                            .width(96.dp)
                                            .clickable {
                                                cropState.setAspectRatio(Ratio.RATIO_4_3)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .width(48.dp)
                                                .aspectRatio(4f / 3f),
                                            color = Color.Gray,
                                            content = {}
                                        )
                                        Text(text = "4:3")
                                    }

                                    Column(
                                        Modifier
                                            .width(96.dp)
                                            .clickable {
                                                cropState.setAspectRatio(Ratio.RATIO_1_1)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .width(48.dp)
                                                .aspectRatio(1f),
                                            color = Color.Gray,
                                            content = {}
                                        )
                                        Text(text = "1:1")
                                    }

                                    Column(
                                        Modifier
                                            .width(96.dp)
                                            .clickable {
                                                cropState.setAspectRatio(Ratio.RATIO_3_4)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .width(48.dp)
                                                .aspectRatio(3f / 4f),
                                            color = Color.Gray,
                                            content = {}
                                        )
                                        Text(text = "3:4")
                                    }

                                    Column(
                                        Modifier
                                            .width(96.dp)
                                            .clickable {
                                                cropState.setAspectRatio(Ratio.RATIO_9_16)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .width(48.dp)
                                                .aspectRatio(9f / 16f),
                                            color = Color.Gray,
                                            content = {}
                                        )
                                        Text(text = "9:16")
                                    }
                                }
                            }

                            null -> Unit
                        }
                    }
                }
            }
        }

        bitmap?.let {
            AsyncImage(
                model = it,
                modifier = Modifier
                    .padding(top = 96.dp, end = 32.dp)
                    .size(128.dp)
                    .align(Alignment.TopEnd)
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun DoneButton(
    imageEditViewModel: ImageEditViewModel,
    cropShape: CropShape,
) {

    IconButton(
        onClick = {
            imageEditViewModel.applyChanges(cropShape.state)
        }) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ShareButton() {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun BackButton() {
    val onBackPressedDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
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

@Composable
fun ImageOnEditView(
    modifier: Modifier = Modifier,
    imageEditViewModel: ImageEditViewModel,
    colorMatrix: ColorMatrix?,
    rotation: Float = 0f,
    uri: Uri,
    crop: Boolean = false,
    cropShape: CropShape = rememberGridCrop(),
) {

    val contrast by imageEditViewModel.contrast.collectAsState()
    val brightness by imageEditViewModel.brightness.collectAsState()
    val blur by imageEditViewModel.blur.collectAsState()

    val updatedColorMatrix = remember(contrast, brightness, colorMatrix) {
        colorMatrix
            .brightnessApplied(brightness)
            .contractionApplied(contrast)
    }


    Crop(
        modifier = modifier,
        drawGrid = crop,
        cropShape = cropShape
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
                .blur((blur).dp * 1.5f),
            model = uri,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            colorFilter = ColorFilter.colorMatrix(updatedColorMatrix),
            onState = {
                if (it is AsyncImagePainter.State.Success) {
                    imageEditViewModel.onImageLoaded(it.result.drawable.toBitmapOrNull())
                }
            }
        )
    }
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
