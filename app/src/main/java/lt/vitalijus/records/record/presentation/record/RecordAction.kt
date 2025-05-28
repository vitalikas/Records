package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.record.presentation.record.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.record.models.FilterItem
import lt.vitalijus.records.record.presentation.record.models.RecordFilterChipType
import lt.vitalijus.records.record.presentation.record.models.TrackSizeInfo

sealed interface RecordAction {

    data class OnFilterChipClick(val chipType: RecordFilterChipType) : RecordAction
    data class OnRemoveFilters(val filterType: RecordFilterChipType) : RecordAction
    data class OnDismissFilterDropDown(val filterType: RecordFilterChipType) : RecordAction
    data class OnFilterByItem(val filterItem: FilterItem) : RecordAction
    data class OnPlayClick(val recordId: Int) : RecordAction
    data object OnPauseClick : RecordAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : RecordAction
    data class OnFabClick(val captureMethod: AudioCaptureMethod) : RecordAction
    data object OnSettingsClick : RecordAction
    data object OnAudioPermissionGranted : RecordAction
}
