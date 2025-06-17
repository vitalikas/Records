@file:OptIn(ExperimentalLayoutApi::class)

package lt.vitalijus.records.record.presentation.records.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.core.presentation.designsystem.chips.HashtagChip
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.util.defaultShadow
import lt.vitalijus.records.record.presentation.components.RecordMoodPlayer
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.models.RecordUi
import lt.vitalijus.records.record.presentation.preview.PreviewModels
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo

@Composable
fun RecordCard(
    recordUi: RecordUi,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeekAudio: (progress: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .defaultShadow(shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recordUi.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = recordUi.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RecordMoodPlayer(
                moodUi = recordUi.mood,
                playbackState = recordUi.playbackState,
                playerProgress = recordUi.playbackRatio,
                durationPlayed = recordUi.playbackCurrentDuration,
                totalPlaybackDuration = recordUi.playbackTotalDuration,
                powerRatios = recordUi.amplitudes,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                onSeekAudio = onSeekAudio,
                onTrackSizeAvailable = onTrackSizeAvailable
            )

            if (!recordUi.note.isNullOrBlank()) {
                RecordExpandableText(text = recordUi.note)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                recordUi.topics.forEach { topic ->
                    HashtagChip(text = topic)
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecordCardPreview() {
    RecordsTheme {
        RecordCard(
            recordUi = PreviewModels.recordUi.copy(
                mood = MoodUi.NEUTRAL
            ),
            onTrackSizeAvailable = {},
            onPlayClick = {},
            onPauseClick = {},
            onSeekAudio = {}
        )
    }
}
