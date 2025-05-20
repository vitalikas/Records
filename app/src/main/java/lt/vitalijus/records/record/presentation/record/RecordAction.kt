package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.record.models.RecordFilterChipType

sealed interface RecordAction {
    data object OnMoodChipClick : RecordAction
    data object OnDismissMoodDropDown : RecordAction
    data class OnFilterByMoodClick(val moodUi: MoodUi) : RecordAction
    data object OnTopicChipClick : RecordAction
    data object OnDismissTopicDropDown : RecordAction
    data class OnFilterByTopicClick(val topic: String) : RecordAction
    data object OnFabClick : RecordAction
    data object OnFabLongClick : RecordAction
    data object OnSettingsClick : RecordAction
    data class OnRemoveFilters(val filterType: RecordFilterChipType) : RecordAction
}
