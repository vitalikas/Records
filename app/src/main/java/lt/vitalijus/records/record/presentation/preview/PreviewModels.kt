package lt.vitalijus.records.record.presentation.preview

import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.models.RecordUi
import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

data object PreviewModels {

    val recordUi = RecordUi(
        id = 0,
        title = "My audio record",
        mood = MoodUi.STRESSED,
        recordedAt = Instant.now(),
        note = buildString {
            repeat(100) {
                append("Hello ")
            }
        },
        topics = listOf("Love", "Work"),
        amplitudes = (1..30).map { Random.nextFloat() },
        playbackTotalDuration = 250.seconds,
        playbackCurrentDuration = 120.seconds,
        playbackState = PlaybackState.PAUSED
    )
}
