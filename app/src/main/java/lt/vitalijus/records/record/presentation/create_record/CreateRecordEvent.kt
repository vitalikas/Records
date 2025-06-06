package lt.vitalijus.records.record.presentation.create_record

sealed interface CreateRecordEvent {

    data object FailedToSaveFile: CreateRecordEvent
}
