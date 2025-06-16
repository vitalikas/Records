package lt.vitalijus.records.record.presentation.create_record

import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo

sealed interface CreateRecordEvent {

    data object SuccessfullySaved: CreateRecordEvent
    data object FailedToSaveFile: CreateRecordEvent
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : CreateRecordEvent
}
