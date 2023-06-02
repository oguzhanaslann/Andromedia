package com.example.andromedia.ui.colorPallette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.andromedia.ui.theme.AndromediaTheme

@Composable
fun ColorShowcaseView(
    modifier: Modifier = Modifier,
    color: Color,
    description: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier
                .size(24.dp),
            shape = CircleShape,
            color = color,
            content = {}
        )
        Text(text = description)
    }
}

@Preview(showBackground = true)
@Composable
fun previewColorShowcaseView() {

    AndromediaTheme {
        Column {
            ColorShowcaseView(
                color = Color.Red,
                description = "Red"
            )
            //#484836
            ColorShowcaseView(
                color = Color(0xFF484836),
                description = "Dark Grey"
            )

            ColorShowcaseView(
                color = Color(0xFF603f26),
                description = "Dark Grey"
            )
        }
    }
}