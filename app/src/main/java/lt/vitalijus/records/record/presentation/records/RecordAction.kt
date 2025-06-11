package lt.vitalijus.records.record.presentation.records

import lt.vitalijus.records.record.presentation.records.models.FilterItem
import lt.vitalijus.records.record.presentation.records.models.RecordFilterChipType
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo

sealed interface RecordAction {

    data class OnFilterChipClick(val chipType: RecordFilterChipType) : RecordAction
    data class OnRemoveFilters(val filterType: RecordFilterChipType) : RecordAction
    data class OnDismissFilterDropDown(val filterType: RecordFilterChipType) : RecordAction
    data class OnFilterByItem(val filterItem: FilterItem) : RecordAction
    data object OnSettingsClick : RecordAction
    data class OnPlayAudioClick(val recordId: Int) : RecordAction
    data object OnPauseAudioClick : RecordAction
    data class OnSeekAudio(val progress: Float) : RecordAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : RecordAction

    data object OnRecordButtonLongClick : RecordAction
    data object OnCancelRecording : RecordAction
    data object OnPauseRecording : RecordAction
    data object OnResumeRecording : RecordAction
    data object OnCompleteRecording : RecordAction
}
