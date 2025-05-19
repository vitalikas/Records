package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.record.presentation.record.models.RecordFilterChip

sealed interface RecordAction {
    data object OnMoodChipClick : RecordAction
    data object OnTopicChipClick : RecordAction
    data object OnFabClick : RecordAction
    data object OnFabLongClick : RecordAction
    data object OnSettingsClick : RecordAction
    data class OnRemoveFilters(val filterType: RecordFilterChip) : RecordAction
}
