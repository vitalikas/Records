package lt.vitalijus.records.record.presentation.record

sealed interface RecordEvent {

    data object RequestAudioPermission : RecordEvent
    data object RecordingTooShort : RecordEvent
    data object OnDoneRecording : RecordEvent
}
