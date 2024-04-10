package com.example.andromedia.ui.record

import android.Manifest
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.linc.audiowaveform.AudioWaveform
import linc.com.amplituda.Amplituda
import java.io.File

@Composable
fun AudioRecorderView() {

    var audioFile by remember { mutableStateOf<File?>(null) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RecordView(
                onRecorded = {
                    Log.e("TAG", "AudioRecorderView: $it")
                    audioFile = File(it)
                }
            )
        }

        audioFile?.let {
            Log.e("TAG", "AudioRecorderView: ")
            val list = Amplituda(LocalContext.current)
                .processAudio(it)
                .get()
                .amplitudesAsList()
                .orEmpty()
                .map { it?.toInt()?.plus(Math.random().times(100).toInt()) ?: 0 }
            Log.e("TAG", "AudioRecorderView: list $list")


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                val context = LocalContext.current
                val audioPlayer = remember {
                    MediaPlayer.create(
                        context,
                        it.toURI()
                            .toString()
                            .toUri()
                    )
                }

                LaunchedEffect(audioPlayer) {
                    audioPlayer.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }



                Row {
                    IconButton(onClick = {
                        audioPlayer.prepare()
                        audioPlayer.start()
                        Log.e("TAG", "AudioRecorderView: audioPlayer.isPlaying ${audioPlayer.isPlaying}")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "play",
                            tint = Color.Black
                        )
                    }
                    PlaybackView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                        amplitudes = list
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordView(
    onRecorded: (outputFile: String) -> Unit = {},
) {
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


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                if (!isRecording) {
                    startRecording(permissionLauncher)
                } else {
                    stopRecording(audioRecorder)
                    audioRecorder = null
                    onRecorded(context.filesDir.absolutePath + "/recording.mp3")
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
        Text(
            text = if (isRecording) "Recording" else "Record",
        )
    }
}

private fun startRecording(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
}

private fun stopRecording(audioRecorder1: MediaRecorder?) {
    var audioRecorder11 = audioRecorder1
    Log.e("TAG", "RecordView: stop recording $audioRecorder11")
    audioRecorder11?.let {
        it.stop()
        it.reset()
        it.release()
    }
}

@Composable
fun PlaybackView(
    modifier: Modifier = Modifier,
    amplitudes: List<Int> = listOf(),
) {
    var waveformProgress by remember { mutableStateOf(0F) }

    AudioWaveform(
        modifier = modifier
            .border(
                width = 4.dp,
                color = Color.Black
            ),
        amplitudes = amplitudes,
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