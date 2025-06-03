package lt.vitalijus.records.record.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.theme.Pause
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.core.presentation.util.defaultShadow
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.records.models.PlaybackState

@Composable
fun RecordPlaybackButton(
    playbackState: PlaybackState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    colors: IconButtonColors,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = when (playbackState) {
            PlaybackState.PLAYING -> onPauseClick
            PlaybackState.PAUSED,
            PlaybackState.STOPPED -> onPlayClick
        },
        colors = colors,
        modifier = modifier
            .defaultShadow()
    ) {
        Icon(
            imageVector = when (playbackState) {
                PlaybackState.PLAYING -> Icons.Filled.Pause
                PlaybackState.PAUSED,
                PlaybackState.STOPPED -> Icons.Filled.PlayArrow
            },
            contentDescription = when (playbackState) {
                PlaybackState.PLAYING -> stringResource(R.string.playing)
                PlaybackState.PAUSED -> stringResource(R.string.paused)
                PlaybackState.STOPPED -> stringResource(R.string.stopped)
            }
        )
    }
}

@Preview
@Composable
private fun RecordPlaybackButtonPreview() {
    RecordsTheme {
        RecordPlaybackButton(
            playbackState = PlaybackState.PLAYING,
            onPlayClick = {},
            onPauseClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MoodUi.SAD.colorSet.vivid
            )
        )
    }
}
