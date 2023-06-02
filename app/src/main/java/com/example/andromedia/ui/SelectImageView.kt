package com.example.andromedia.ui

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.andromedia.R

@Composable
fun SelectImageView(
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

@Composable
fun ImageSelectDispatchedView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    image : Uri? = null,
    imageContent : @Composable (Modifier ,Uri) -> Unit = {_, _ -> }
) {
    when(image) {
        null -> SelectImageView(
            modifier = modifier,
            onClick = onClick
        )
        else -> imageContent(modifier, image)
    }

}