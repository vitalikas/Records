package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.record.models.FilterItem
import lt.vitalijus.records.record.presentation.record.models.RecordFilterChipType

sealed interface RecordAction {

    data class OnFilterChipClick(val chipType: RecordFilterChipType) : RecordAction
    data class OnRemoveFilters(val filterType: RecordFilterChipType) : RecordAction
    data class OnDismissFilterDropDown(val filterType: RecordFilterChipType) : RecordAction
    data class OnFilterByItem(val filterItem: FilterItem) : RecordAction
    data object OnFabClick : RecordAction
    data object OnFabLongClick : RecordAction
    data object OnSettingsClick : RecordAction
}
