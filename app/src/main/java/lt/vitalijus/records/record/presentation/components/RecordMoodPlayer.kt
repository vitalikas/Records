package lt.vitalijus.records.record.presentation.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.core.presentation.designsystem.theme.MoodPrimary25
import lt.vitalijus.records.core.presentation.designsystem.theme.MoodPrimary35
import lt.vitalijus.records.core.presentation.designsystem.theme.MoodPrimary80
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.util.formatMMSS
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun RecordMoodPlayer(
    moodUi: MoodUi?,
    playbackState: PlaybackState,
    playerProgress: () -> Float,
    durationPlayed: Duration,
    totalPlaybackDuration: Duration,
    powerRatios: List<Float>,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    amplitudeBarWidth: Dp = 5.dp,
    amplitudeBarSpacing: Dp = 4.dp,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit
) {
    val iconTint = when (moodUi) {
        null -> MoodPrimary80
        else -> moodUi.colorSet.vivid
    }
    val trackFillColor = when (moodUi) {
        null -> MoodPrimary80
        else -> moodUi.colorSet.vivid
    }
    val backgroundColor = when (moodUi) {
        null -> MoodPrimary25
        else -> moodUi.colorSet.faded
    }
    val trackColor = when (moodUi) {
        null -> MoodPrimary35
        else -> moodUi.colorSet.desaturated
    }

    val formattedDurationText = remember(durationPlayed, totalPlaybackDuration) {
        "${durationPlayed.formatMMSS()}/${totalPlaybackDuration.formatMMSS()}"
    }

    val density = LocalDensity.current

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecordPlaybackButton(
                playbackState = playbackState,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = iconTint
                ),
                modifier = Modifier
            )

            RecordPlayBar(
                amplitudeBarWidth = amplitudeBarWidth,
                amplitudeBarSpacing = amplitudeBarSpacing,
                powerRatios = powerRatios,
                trackColor = trackColor,
                trackFillColor = trackFillColor,
                playerProgress = playerProgress,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
                    .fillMaxHeight()
                    .onSizeChanged {
                        if (it.width > 0) {
                            onTrackSizeAvailable(
                                TrackSizeInfo(
                                    trackWidth = it.width.toFloat(),
                                    barWidth = with(density) {
                                        amplitudeBarWidth.toPx()
                                    },
                                    spacing = with(density) {
                                        amplitudeBarSpacing.toPx()
                                    }
                                )
                            )
                        }
                    }
            )

            Text(
                text = formattedDurationText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFeatureSettings = "tnum"
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(end = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun RecordMoodPlayerPreview() {
    RecordsTheme {
        val ratios = remember {
            (1..25).map {
                Random.nextFloat()
            }
        }

        RecordMoodPlayer(
            moodUi = MoodUi.NEUTRAL,
            playbackState = PlaybackState.PAUSED,
            playerProgress = { 0.27f },
            durationPlayed = 125.seconds,
            totalPlaybackDuration = 250.seconds,
            powerRatios = ratios,
            onTrackSizeAvailable = {},
            onPlayClick = {},
            onPauseClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
