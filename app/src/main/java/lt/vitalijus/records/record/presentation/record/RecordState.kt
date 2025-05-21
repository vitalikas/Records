package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.record.presentation.record.models.FilterChip

data class RecordState(
    val moodFilterChipData: FilterChip.MoodFilterChip = FilterChip.MoodFilterChip(),
    val topicFilterChipData: FilterChip.TopicFilterChip = FilterChip.TopicFilterChip(),
    val hasRecorded: Boolean = false,
    val isLoadingData: Boolean = false
)
