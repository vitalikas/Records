package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.record.domain.recording.RecordingDetails
import lt.vitalijus.records.record.presentation.record.models.AudioCaptureMethod

sealed interface RecordEvent {

    sealed interface AudioPermission : RecordEvent {
        data class OnRequest(val captureMethod: AudioCaptureMethod) : AudioPermission
        data object OnGranted : AudioPermission
    }

    sealed interface RecordState: RecordEvent {
        data object OnTooShort : RecordState
        data class OnDone(val recordingDetails: RecordingDetails) : RecordState
    }
}
