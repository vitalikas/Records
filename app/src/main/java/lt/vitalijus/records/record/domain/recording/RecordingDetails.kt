package lt.vitalijus.records.record.domain.recording

import kotlin.time.Duration

data class RecordingDetails(
    val duration: Duration = Duration.ZERO,
    val amplitudes: List<Float> = emptyList(),
    val tempFilePath: String? = null
)
