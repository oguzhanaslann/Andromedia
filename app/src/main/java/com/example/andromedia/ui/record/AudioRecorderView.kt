package com.example.andromedia.ui.record

import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.AudioWaveform

@Composable
fun AudioRecorderView() {
    Box {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RecordView()
        }
    }
}

@Composable
fun RecordView() {



    val context = LocalContext.current
    var audioRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    val isRecording = remember(audioRecorder) { audioRecorder != null }
    val icon = if (isRecording) {
        Icons.Filled.Pause
    } else {
        Icons.Filled.Mic
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                audioRecorder = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> MediaRecorder(context)
                    else -> MediaRecorder()
                }

                audioRecorder?.let {
                    try {
                        it.setAudioSource(MediaRecorder.AudioSource.MIC)
                        it.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        it.setOutputFile(context.filesDir.absolutePath + "/recording.mp3")
                        it.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        it.prepare()
                        it.start()
                    } catch (e: Exception) {
                        Log.e("LOG_TAG", "${e.message}")
                    }
                }
            }
        }


    IconButton(
        onClick = {
            if (!isRecording) {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            } else {
                Log.e("TAG", "RecordView: stop recording $audioRecorder")
                audioRecorder?.let {
                    it.stop()
                    it.reset()
                    it.release()
                    audioRecorder = null
                }
            }
        }
    ) {
        Icon(
            modifier = Modifier.size(96.dp),
            imageVector = icon,
            contentDescription = if (isRecording) "pause" else "record",
            tint = Color.Black
        )
    }
}

@Composable
fun PlaybackView(
    modifier: Modifier = Modifier,
) {
    var waveformProgress by remember { mutableStateOf(0F) }
    AudioWaveform(
        modifier = modifier,
        amplitudes = listOf<Int>(),
        waveformBrush = SolidColor(Color.LightGray),
        progress = waveformProgress,
        onProgressChange = { waveformProgress = it }
    )
}

@Preview
@Composable
fun previewAudioRecorderView() {
    AudioRecorderView()
}