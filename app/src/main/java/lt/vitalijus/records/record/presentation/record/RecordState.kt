package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.models.RecordUi
import lt.vitalijus.records.record.presentation.record.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.record.models.FilterChip
import lt.vitalijus.records.record.presentation.record.models.RecordDaySection
import lt.vitalijus.records.record.presentation.record.models.RecordingState
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration

data class RecordState(
    val records: Map<UiText, List<RecordUi>> = emptyMap(),
    val currentCaptureMethod: AudioCaptureMethod? = null,
    val recordingState: RecordingState = RecordingState.NOT_RECORDING,
    val recordingElapsedDuration: Duration = Duration.ZERO,
    val moodFilterChipData: FilterChip.MoodFilterChip = FilterChip.MoodFilterChip(),
    val topicFilterChipData: FilterChip.TopicFilterChip = FilterChip.TopicFilterChip(),
    val hasRecorded: Boolean = false,
    val isLoadingData: Boolean = false
) {
    val recordDaySections = records
        .toList()
        .map { (dateHeader, records) ->
            RecordDaySection(
                dateHeader = dateHeader,
                records = records
            )
        }

    val formattedRecordDuration: String
        get() {
            val minutes = (recordingElapsedDuration.inWholeMinutes % 60).toInt()
            val seconds = (recordingElapsedDuration.inWholeSeconds % 60).toInt()
            val centiseconds =
                ((recordingElapsedDuration.inWholeMilliseconds % 1000) / 10.0).roundToInt()

            return String.format(
                locale = Locale.US,
                format = "%02d:%02d:%02d",
                minutes, seconds, centiseconds
            )
        }
}
