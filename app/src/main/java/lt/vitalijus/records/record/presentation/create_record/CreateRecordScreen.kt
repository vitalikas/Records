@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.records.record.presentation.create_record

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.buttons.PrimaryButton
import lt.vitalijus.records.core.presentation.designsystem.buttons.SecondaryButton
import lt.vitalijus.records.core.presentation.designsystem.text_fields.TransparentHintTextField
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.designsystem.theme.secondary70
import lt.vitalijus.records.core.presentation.designsystem.theme.secondary95
import lt.vitalijus.records.core.presentation.util.ObserveAsEvents
import lt.vitalijus.records.core.presentation.util.asString
import lt.vitalijus.records.record.presentation.components.RecordMoodPlayer
import lt.vitalijus.records.record.presentation.create_record.components.SelectMoodSheet
import lt.vitalijus.records.record.presentation.create_record.components.TopicsRow
import lt.vitalijus.records.record.presentation.models.MoodUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateRecordRoot(
    onConfirmLeave: () -> Unit,
    viewModel: CreateRecordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateRecordEvent.FailedToSaveFile -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_couldnt_save_file),
                    Toast.LENGTH_LONG
                ).show()

                onConfirmLeave()
            }

            is CreateRecordEvent.OnTrackSizeAvailable -> Unit

            CreateRecordEvent.SuccessfullySaved -> {
                onConfirmLeave()
            }
        }
    }

    CreateRecordScreen(
        state = state,
        onAction = viewModel::onAction,
        onEvent = viewModel::onEvent,
        onConfirmLeave = onConfirmLeave
    )
}

@Composable
fun CreateRecordScreen(
    state: CreateRecordState,
    onAction: (CreateRecordAction) -> Unit,
    onEvent: (CreateRecordEvent) -> Unit,
    onConfirmLeave: () -> Unit
) {
    BackHandler(
        enabled = !state.showConfirmLeaveDialog
    ) {
        onAction(CreateRecordAction.OnSystemGoBackClick)
    }

    Scaffold(
        contentColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.new_entry),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(CreateRecordAction.OnNavigateBackClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        val descriptionFocusRequester = remember {
            FocusRequester()
        }
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.moodUi == null) {
                    FilledIconButton(
                        onClick = {
                            onAction(CreateRecordAction.OnSelectMoodClick)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary95,
                            contentColor = MaterialTheme.colorScheme.secondary70
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_mood)
                        )
                    }
                } else {
                    Image(
                        imageVector = ImageVector.vectorResource(state.moodUi.iconSet.fill),
                        contentDescription = state.moodUi.title.asString(),
                        modifier = Modifier
                            .height(32.dp)
                            .clickable {
                                onAction(CreateRecordAction.OnSelectMoodClick)
                            },
                        contentScale = ContentScale.FillHeight
                    )
                }

                TransparentHintTextField(
                    text = state.titleText,
                    onValueChange = {
                        onAction(CreateRecordAction.OnAddTitleTextChange(it))
                    },
                    modifier = Modifier
                        .weight(1f),
                    hintText = stringResource(R.string.add_title),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            descriptionFocusRequester.requestFocus()
                        }
                    )
                )
            }

            RecordMoodPlayer(
                moodUi = state.moodUi,
                playbackState = state.playbackState,
                playerProgress = state.durationPlayedRatio,
                durationPlayed = state.durationPlayed,
                totalPlaybackDuration = state.playbackTotalDuration,
                powerRatios = state.playbackAmplitudes,
                onPlayClick = {
                    onAction(CreateRecordAction.OnPlayAudioClick)
                },
                onPauseClick = {
                    onAction(CreateRecordAction.OnPauseAudioClick)
                },
                onSeekAudio = {
                    onAction(CreateRecordAction.OnSeekAudio(it))
                },
                onTrackSizeAvailable = {
                    onEvent(CreateRecordEvent.OnTrackSizeAvailable(it))
                }
            )

            TopicsRow(
                topics = state.topics,
                addTopicText = state.addTopicText,
                showCreateTopicOption = state.showCreateTopicOption,
                showTopicSuggestions = state.showTopicSuggestions,
                searchResults = state.searchResult,
                onTopicClick = {
                    onAction(CreateRecordAction.OnTopicClick(it))
                },
                onDismissTopicSuggestions = {
                    onAction(CreateRecordAction.OnDismissTopicSuggestions)
                },
                onRemoveTopicClick = {
                    onAction(CreateRecordAction.OnRemoveTopicClick(it))
                },
                onAddTopicTextChange = {
                    onAction(CreateRecordAction.OnAddTopicTextChange(it))
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = stringResource(R.string.add_description),
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier
                        .size(16.dp)
                )

                TransparentHintTextField(
                    text = state.noteText,
                    onValueChange = {
                        onAction(CreateRecordAction.OnAddNoteTextChange(it))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(descriptionFocusRequester),
                    hintText = stringResource(R.string.add_description),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cancel),
                    onClick = {
                        onAction(CreateRecordAction.OnCancelClick)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                )

                PrimaryButton(
                    text = stringResource(R.string.save),
                    onClick = {
                        onAction(CreateRecordAction.OnSaveClick)
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = state.canSaveRecord,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                            modifier = Modifier
                                .size(16.dp)
                        )
                    }
                )
            }
        }

        if (state.showMoodSelector) {
            SelectMoodSheet(
                selectedMood = state.selectedMoodUi,
                onMoodClick = {
                    onAction(CreateRecordAction.OnMoodClick(it))
                },
                onDismiss = {
                    onAction(CreateRecordAction.OnDismissMoodSelector)
                },
                onConfirmClick = {
                    onAction(CreateRecordAction.OnConfirmMood)
                }
            )
        }

        if (state.showConfirmLeaveDialog) {
            AlertDialog(
                onDismissRequest = {
                    onAction(CreateRecordAction.OnDismissConfirmLeaveDialog)
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirmLeave
                    ) {
                        Text(
                            text = stringResource(R.string.discard),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onAction(CreateRecordAction.OnDismissConfirmLeaveDialog)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.discard_recording)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.this_cannot_be_undone)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun CreateRecordPreview() {
    RecordsTheme {
        CreateRecordScreen(
            state = CreateRecordState(
                moodUi = MoodUi.EXCITED,
                showMoodSelector = false,
                showConfirmLeaveDialog = false
            ),
            onAction = {},
            onEvent = {},
            onConfirmLeave = {}
        )
    }
}
