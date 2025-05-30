package lt.vitalijus.records.record.presentation.util

import lt.vitalijus.records.app.navigation.NavigationRoute
import lt.vitalijus.records.record.domain.recording.RecordingDetails
import kotlin.time.Duration.Companion.milliseconds

fun RecordingDetails.toCreateRecordRoute(): NavigationRoute.CreateRecord {
    return NavigationRoute.CreateRecord(
        recordingPath = this.filePath
            ?: throw IllegalStateException("Recording path can't be null!"),
        duration = this.duration.inWholeMilliseconds,
        amplitudes = this.amplitudes.joinToString(";")
    )
}

fun NavigationRoute.CreateRecord.toRecordDetails(): RecordingDetails {
    return RecordingDetails(
        duration = this.duration.milliseconds,
        amplitudes = this.amplitudes.split(";").map { it.toFloat() },
        filePath = this.recordingPath
    )
}
