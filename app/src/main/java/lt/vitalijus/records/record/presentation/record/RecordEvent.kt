package lt.vitalijus.records.record.presentation.record

sealed interface RecordEvent {

    data object RequestAudioPermission : RecordEvent
}
