package lt.vitalijus.records.record.presentation.record

import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem.Companion.asUnselectedItems
import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.record.models.MoodChipContent
import lt.vitalijus.records.record.presentation.record.models.RecordFilterChipType

data class RecordState(
    val hasRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val hasActiveMoodFilters: Boolean = false,
    val isLoadingData: Boolean = false,
    val moods: List<SelectableItem<MoodUi>> = emptyList(),
    val topics: List<SelectableItem<String>> = listOf("Love", "Happy", "Work").asUnselectedItems(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val selectedFilterChipType: RecordFilterChipType? = null,
    val topicChipTitle: UiText = UiText.StringResource(R.string.all_topics)
)
