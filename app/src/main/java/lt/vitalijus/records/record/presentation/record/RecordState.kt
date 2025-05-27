package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.models.RecordUi
import lt.vitalijus.records.record.presentation.record.models.FilterChip
import lt.vitalijus.records.record.presentation.record.models.RecordDaySection

data class RecordState(
    val records: Map<UiText, List<RecordUi>> = emptyMap(),
    val moodFilterChipData: FilterChip.MoodFilterChip = FilterChip.MoodFilterChip(),
    val topicFilterChipData: FilterChip.TopicFilterChip = FilterChip.TopicFilterChip(),
    val hasRecorded: Boolean = false,
    val isLoadingData: Boolean = false
) {
    val recordDaySections = records
        .toList()
        .map { (dateHeader, records) ->
            RecordDaySection(
                dateHeader = dateHeader,
                records = records
            )
        }
}
