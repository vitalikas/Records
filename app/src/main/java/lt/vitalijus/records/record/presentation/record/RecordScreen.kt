package lt.vitalijus.records.record.presentation.record

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.designsystem.theme.bgGradient
import lt.vitalijus.records.record.presentation.record.components.RecordFilterRow
import lt.vitalijus.records.record.presentation.record.components.RecordFloatingActionButton
import lt.vitalijus.records.record.presentation.record.components.RecordList
import lt.vitalijus.records.record.presentation.record.components.RecordsEmptyBackground
import lt.vitalijus.records.record.presentation.record.components.RecordsTopBar

@Composable
fun RecordRoot(
    viewModel: RecordViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                    onAction(RecordAction.OnFabClick)
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
                            onAction(RecordAction.OnPlayClick(recordId))
                        },
                        onPauseClick = {
                            onAction(RecordAction.OnPauseClick)
                        },
                        onTrackSizeAvailable = { trackSizeInfo ->
                            onAction(RecordAction.OnTrackSizeAvailable(trackSizeInfo))
                        }
                    )
                }
            }
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
