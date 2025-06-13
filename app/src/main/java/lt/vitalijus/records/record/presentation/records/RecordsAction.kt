package lt.vitalijus.records.record.presentation.records

import lt.vitalijus.records.record.presentation.records.models.FilterItem
import lt.vitalijus.records.record.presentation.records.models.RecordFilterChipType
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo

sealed interface RecordsAction {

    data class OnFilterChipClick(val chipType: RecordFilterChipType) : RecordsAction
    data class OnRemoveFilters(val filterType: RecordFilterChipType) : RecordsAction
    data class OnDismissFilterDropDown(val filterType: RecordFilterChipType) : RecordsAction
    data class OnFilterByItem(val filterItem: FilterItem) : RecordsAction
    data object OnSettingsClick : RecordsAction
    data class OnPlayAudioClick(val recordId: Int) : RecordsAction
    data object OnPauseAudioClick : RecordsAction
    data class OnSeekAudio(val progress: Float) : RecordsAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : RecordsAction

    data object OnRecordsButtonLongClick : RecordsAction
    data object OnCancelRecording : RecordsAction
    data object OnPauseRecording : RecordsAction
    data object OnResumeRecording : RecordsAction
    data object OnCompleteRecording : RecordsAction
}
