package lt.vitalijus.records.record.presentation.record.models

data class MoodFilterConfig(
    val chipContent: MoodChipItemContent,
    val hasActiveFilters: Boolean,
    val isSelected: Boolean, // true if RecordFilterChipType.MOOD is selected

)
