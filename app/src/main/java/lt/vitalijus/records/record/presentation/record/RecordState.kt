package lt.vitalijus.records.record.presentation.record

data class RecordState(
    val hasRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val hasActiveMoodFilters: Boolean = false,
    val isLoadingData: Boolean = false,
)
