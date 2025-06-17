@file:OptIn(ExperimentalFoundationApi::class)

package lt.vitalijus.records.record.presentation.records.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.preview.PreviewModels
import lt.vitalijus.records.record.presentation.records.models.RecordDaySection
import lt.vitalijus.records.record.presentation.records.models.RelativePosition
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo
import java.time.Instant
import java.time.ZonedDateTime

@Composable
fun RecordList(
    sections: List<RecordDaySection>,
    onPlayClick: (recordId: Int) -> Unit,
    onPauseClick: () -> Unit,
    onSeekAudio: (recordId: Int, progress: Float) -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        sections.forEachIndexed { sectionIndex, section ->
            stickyHeader {
                if (sectionIndex > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = section.dateHeader.asString().uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(
                items = section.records,
                key = { index, record ->
                    record.id
                }
            ) { index, record ->
                RecordTimelineItem(
                    recordUi = record,
                    relativePosition = when {
                        index == 0 && section.records.size == 1 -> RelativePosition.SINGLE_ENTRY
                        index == 0 -> RelativePosition.FIRST
                        index == section.records.lastIndex -> RelativePosition.LAST
                        else -> RelativePosition.IN_BETWEEN
                    },
                    onPlayClick = {
                        onPlayClick(record.id)
                    },
                    onPauseClick = onPauseClick,
                    onSeekAudio = { progress ->
                        onSeekAudio(record.id, progress)
                    },
                    onTrackSizeAvailable = onTrackSizeAvailable
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun RecordListPreview() {
    RecordsTheme {
        val todaysRecords = remember {
            (1..3).map {
                PreviewModels.recordUi.copy(
                    id = it,
                    recordedAt = Instant.now()
                )
            }
        }

        val yesterdaysRecords = remember {
            (4..6).map {
                PreviewModels.recordUi.copy(
                    id = it,
                    recordedAt = ZonedDateTime.now().minusDays(1).toInstant()
                )
            }
        }

        val recordsFrom2DaysAgo = remember {
            (7..9).map {
                PreviewModels.recordUi.copy(
                    id = it,
                    recordedAt = ZonedDateTime.now().minusDays(2).toInstant()
                )
            }
        }

        val sections = remember {
            listOf(
                RecordDaySection(
                    dateHeader = UiText.Dynamic("Today"),
                    records = todaysRecords
                ),
                RecordDaySection(
                    dateHeader = UiText.Dynamic("Yesterday"),
                    records = yesterdaysRecords
                ),
                RecordDaySection(
                    dateHeader = UiText.Dynamic("2025/04/25"),
                    records = recordsFrom2DaysAgo
                )
            )
        }

        RecordList(
            sections = sections,
            onPlayClick = {},
            onPauseClick = {},
            onSeekAudio = { _, _ ->
            },
            onTrackSizeAvailable = {}
        )
    }
}
