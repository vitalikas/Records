package lt.vitalijus.records.record.presentation.records

import lt.vitalijus.records.record.domain.recording.RecordingDetails
import lt.vitalijus.records.record.presentation.records.models.AudioCaptureMethod

sealed interface RecordsEvent {

    sealed interface AudioPermission : RecordsEvent {
        data class OnRequest(val captureMethod: AudioCaptureMethod) : AudioPermission
        data object OnGranted : AudioPermission
    }

    sealed interface RecordsState: RecordsEvent {
        data object OnTooShort : RecordsState
        data class OnDone(val recordingDetails: RecordingDetails) : RecordsState
    }
}
