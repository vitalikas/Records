package lt.vitalijus.records.record.presentation.models

import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import lt.vitalijus.records.record.presentation.util.toReadableTime
import kotlin.time.Duration
import java.time.Instant as JavaInstant

data class RecordUi(
    val id: Int,
    val title: String,
    val mood: MoodUi,
    val recordedAt: JavaInstant,
    val note: String?,
    val topics: List<String>,
    val amplitudes: List<Float>,
    val audioFilePath: String,
    val playbackTotalDuration: Duration,
    val playbackCurrentDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED
) {
    val formattedDate: String
        get() = recordedAt.toReadableTime()
    val playbackRatio = (playbackCurrentDuration / playbackTotalDuration).toFloat()
}
