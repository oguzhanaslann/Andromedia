package com.example.andromedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.andromedia.ui.imageEdit.ImageEditView
import com.example.andromedia.ui.ShapeableImage
import com.example.andromedia.ui.ambientColor.AmbientColorView
import com.example.andromedia.ui.colorPallette.ColorPaletteView
import com.example.andromedia.ui.imageEdit.ImageEditView
import com.example.andromedia.ui.record.AudioRecorderView
import com.example.andromedia.ui.theme.AndromediaTheme

const val Crop = 0
const val FillBounds = 1
const val FillHeight = 2
const val FillWidth = 3
const val Inside = 4
const val Fit = 5

const val MENU = "menu"

/**
 *  there will an menu to select the options,
 *
 *  image edit view -> crop, rotate,color filter image.
 *  download image -> download image from url.
 *  color pallette api -> get color pallette from image.
 *
 */


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndromediaTheme {
               MainView(pages)
            }
        }
    }

}

private val pages = listOf(
    Page(
        route = "trial",
        title = "Trial",
        content = { Trial() }
    ),

    Page(
        route = "imageEdit",
        title = "image edit",
        content = { /*ImageEditView() */}
    ),

    // color pallette api
    Page(
        route = "colorPallette",
        title = "color pallette",
        content = { ColorPaletteView() }
    ),

    // audio recorder
    Page(
        route = "audioRecorder",
        title = "audio recorder",
        content = { /*AudioRecorderView()*/ }
    ),

    Page(
        route = "ambientColor",
        title = "ambient color",
        content = { AmbientColorView() }
    )
)

@Composable
private fun MainView(pages: List<Page>) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MENU
    ) {
        composable(MENU) {
            MenuView(
                modifier = Modifier
                    .fillMaxSize(),
                pages = pages,
                onRouteClicked = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable("trial") {
            Trial()
        }

        composable("imageEdit") {
            ImageEditView()
        }

        composable("colorPallette") {
            ColorPaletteView()
        }

        composable("audioRecorder") {
            AudioRecorderView()
        }

        composable("ambientColor") {
            AmbientColorView()
        }
    }
}


@Composable
private fun MenuView(
    modifier: Modifier = Modifier,
    pages: List<Page>,
    onRouteClicked: (String) -> Unit = {},
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(pages) { page ->
            Surface(
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable {
                            onRouteClicked(page.route)
                        }
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = page.title
                    )
                }
            }
        }
    }
}

@Composable
fun Trial() {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(9) {
            Box {
                ShapeableImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    imageUrl =
                    "https://images.unsplash.com/photo-1543466835-00a7907e9de1?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2148&q=80",
                    contentDescription = null,
                    contentScale = when {
                        it <= Fit -> getContentScale(it)
                        else -> ContentScale.Crop
                    },
                    loadingContent = {
                        CircularProgressIndicator()
                    },
                    colorFilter = when {
                        it > Fit -> ColorFilter.colorMatrix(ColorMatrix(
                            values = floatArrayOf(
                                1f, 0f, 0f, 0f, 0f,
                                0f, 1f, 0f, 0f, 0f,
                                0f, 0f, 1f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f

                            )
                        ).apply { setToSaturation(0f) })

                        else -> null
                    }
                )

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Type : ${getContentScaleName(it)}",
                    color = Color.White
                )
            }
        }

    }
}

private fun getContentScale(it: Int): ContentScale {
    return when (it) {
        Crop -> ContentScale.Crop
        FillBounds -> ContentScale.FillBounds
        FillHeight -> ContentScale.FillHeight
        FillWidth -> ContentScale.FillWidth
        Inside -> ContentScale.Inside
        Fit -> ContentScale.Fit
        else -> ContentScale.None
    }
}

private fun getContentScaleName(it: Int): String {
    return when (it) {
        Crop -> "Crop"
        FillBounds -> "FillBounds"
        FillHeight -> "FillHeight"
        FillWidth -> "FillWidth"
        Inside -> "Inside"
        Fit -> "Fit"
        else -> "None"
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndromediaTheme {
        MainView(
            pages = pages
        )
    }
}