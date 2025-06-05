package lt.vitalijus.records.record.presentation.records

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.designsystem.theme.bgGradient
import lt.vitalijus.records.core.presentation.util.ObserveAsEvents
import lt.vitalijus.records.core.presentation.util.isAppInForeground
import lt.vitalijus.records.record.domain.recording.RecordingDetails
import lt.vitalijus.records.record.presentation.records.components.RecordDraggableFloatingActionButton
import lt.vitalijus.records.record.presentation.records.components.RecordFilterRow
import lt.vitalijus.records.record.presentation.records.components.RecordList
import lt.vitalijus.records.record.presentation.records.components.RecordRecordingSheet
import lt.vitalijus.records.record.presentation.records.components.RecordsEmptyBackground
import lt.vitalijus.records.record.presentation.records.components.RecordsTopBar
import lt.vitalijus.records.record.presentation.records.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.records.models.RecordingState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun RecordsRoot(
    onNavigateToCreateRecord: (RecordingDetails) -> Unit,
    viewModel: RecordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && state.currentCaptureMethod == AudioCaptureMethod.STANDARD) {
            viewModel.onEvent(RecordEvent.AudioPermission.OnGranted)
        }
    }

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RecordEvent.AudioPermission.OnRequest -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            is RecordEvent.RecordState.OnDone -> {
                Timber.d("Recording done")
                onNavigateToCreateRecord(event.recordingDetails)
            }

            RecordEvent.RecordState.OnTooShort -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.audio_recording_was_too_short),
                    Toast.LENGTH_LONG
                ).show()
            }

            RecordEvent.AudioPermission.OnGranted -> {

            }
        }
    }

    val isAppInForeground by isAppInForeground()
    LaunchedEffect(isAppInForeground, state.currentCaptureMethod) {
        if (state.currentCaptureMethod == AudioCaptureMethod.STANDARD && !isAppInForeground) {
            viewModel.onAction(RecordAction.OnPauseRecording)
        }
    }

    RecordScreen(
        state = state,
        onAction = viewModel::onAction,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun RecordScreen(
    state: RecordState,
    onAction: (RecordAction) -> Unit,
    onEvent: (RecordEvent) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            RecordDraggableFloatingActionButton(
                onClick = {
                    onEvent(
                        RecordEvent.AudioPermission.OnRequest(
                            captureMethod = AudioCaptureMethod.STANDARD
                        )
                    )
                },
                isQuickRecording = state.currentCaptureMethod == AudioCaptureMethod.QUICK,
                onLongPressStart = {
                    val hasPermission = ContextCompat
                        .checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        onAction(RecordAction.OnRecordButtonLongClick)
                    } else {
                        onEvent(
                            RecordEvent.AudioPermission.OnRequest(
                                captureMethod = AudioCaptureMethod.QUICK
                            )
                        )
                    }
                },
                onLongPressEnd = { cancelledRecording ->
                    if (cancelledRecording) {
                        onAction(RecordAction.OnCancelRecording)
                    } else {
                        onAction(RecordAction.OnCompleteRecording)
                    }
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

        if (state.currentCaptureMethod == AudioCaptureMethod.STANDARD &&
            (state.recordingState == RecordingState.RECORDING || state.recordingState == RecordingState.PAUSED)
        ) {
            RecordRecordingSheet(
                formattedRecordDuration = state.formattedRecordDuration,
                isRecording = state.recordingState == RecordingState.RECORDING,
                onDismiss = { onAction(RecordAction.OnCancelRecording) },
                onPauseClick = { onAction(RecordAction.OnPauseRecording) },
                onResumeClick = { onAction(RecordAction.OnResumeRecording) },
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
            onAction = {},
            onEvent = {}
        )
    }
}
