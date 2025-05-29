package lt.vitalijus.records.record.presentation.record

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.designsystem.theme.bgGradient
import lt.vitalijus.records.core.presentation.util.ObserveAsEvents
import lt.vitalijus.records.core.presentation.util.isAppInForeground
import lt.vitalijus.records.record.presentation.record.components.RecordFilterRow
import lt.vitalijus.records.record.presentation.record.components.RecordFloatingActionButton
import lt.vitalijus.records.record.presentation.record.components.RecordList
import lt.vitalijus.records.record.presentation.record.components.RecordRecordingSheet
import lt.vitalijus.records.record.presentation.record.components.RecordsEmptyBackground
import lt.vitalijus.records.record.presentation.record.components.RecordsTopBar
import lt.vitalijus.records.record.presentation.record.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.record.models.RecordingState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun RecordRoot(
    viewModel: RecordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && state.currentCaptureMethod == AudioCaptureMethod.STANDARD) {
            viewModel.onAction(RecordAction.OnAudioPermissionGranted)
        }
    }

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RecordEvent.RequestAudioPermission -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            RecordEvent.OnDoneRecording -> {
                Timber.d("Recording done")
            }

            RecordEvent.RecordingTooShort -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.audio_recording_was_too_short),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val isAppInForeground by isAppInForeground()
    LaunchedEffect(isAppInForeground, state.recordingState) {
        if (state.recordingState == RecordingState.NORMAL_CAPTURE && !isAppInForeground) {
            viewModel.onAction(RecordAction.OnPauseRecordingClick)
        }
    }

    RecordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun RecordScreen(
    state: RecordState,
    onAction: (RecordAction) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            RecordFloatingActionButton(
                onClick = {
                    onAction(
                        RecordAction.OnFabClick(captureMethod = AudioCaptureMethod.STANDARD)
                    )
                }
            )
        },
        topBar = {
            RecordsTopBar(
                onSettingsClick = {
                    onAction(RecordAction.OnSettingsClick)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = MaterialTheme.colorScheme.bgGradient
                )
                .padding(innerPadding)
        ) {
            RecordFilterRow(
                moodFilterChip = state.moodFilterChipData,
                topicFilterChip = state.topicFilterChipData,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxWidth()
            )

            when {
                state.isLoadingData -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                !state.hasRecorded -> {
                    RecordsEmptyBackground(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                else -> {
                    RecordList(
                        sections = state.recordDaySections,
                        onPlayClick = { recordId ->
                            onAction(RecordAction.OnPlayAudioClick(recordId))
                        },
                        onPauseClick = {
                            onAction(RecordAction.OnPauseAudioClick)
                        },
                        onTrackSizeAvailable = { trackSizeInfo ->
                            onAction(RecordAction.OnTrackSizeAvailable(trackSizeInfo))
                        }
                    )
                }
            }
        }

        if (state.recordingState in listOf(RecordingState.NORMAL_CAPTURE, RecordingState.PAUSED)) {
            RecordRecordingSheet(
                formattedRecordDuration = state.formattedRecordDuration,
                isRecording = state.recordingState == RecordingState.NORMAL_CAPTURE,
                onDismiss = { onAction(RecordAction.OnCancelRecordingClick) },
                onPauseClick = { onAction(RecordAction.OnPauseRecordingClick) },
                onResumeClick = { onAction(RecordAction.OnResumeRecordingClick) },
                onCompleteRecording = { onAction(RecordAction.OnCompleteRecording) }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RecordsTheme {
        RecordScreen(
            state = RecordState(
                isLoadingData = false,
                hasRecorded = false
            ),
            onAction = {}
        )
    }
}
