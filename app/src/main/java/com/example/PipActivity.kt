package com.example

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.andromedia.ui.theme.AndromediaTheme

class PipActivity : ComponentActivity() {

    private val isPIPSupported: Boolean
        get() = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndromediaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(
                        modifier = Modifier.clickable {
                            val startIntent: Intent = Intent(
                                this, PipActivity::class.java
                            )
                            startIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            startActivity(startIntent)
                        },
                        "Android"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pipParams() = PictureInPictureParams.Builder()

        .build()

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (!isPIPSupported) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(pipParams())
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    name: String,
) {
    Text(

        text = "Hello $name!", modifier = modifier
    )
}

