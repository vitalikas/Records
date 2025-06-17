package lt.vitalijus.records.record.presentation.util

import lt.vitalijus.records.record.domain.record.Record
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.models.RecordUi
import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import kotlin.time.Duration

fun Record.toRecordUi(
    currentPlaybackDuration: Duration = Duration.ZERO,
    playbackState: PlaybackState = PlaybackState.STOPPED
): RecordUi {
    return RecordUi(
        id = checkNotNull(id) {
            "Record id cannot be null."
        },
        title = title,
        mood = MoodUi.valueOf(mood.name),
        recordedAt = recordedAt,
        note = note,
        topics = topics,
        amplitudes = audioAmplitudes,
        audioFilePath = audioFilePath,
        playbackTotalDuration = audioPlaybackLength,
        playbackCurrentDuration = currentPlaybackDuration,
        playbackState = playbackState
    )
}
