package lt.vitalijus.records.record.presentation.records.models

import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem.Companion.asUnselectedItems
import lt.vitalijus.records.record.presentation.models.MoodUi

sealed interface FilterChip<T> {
    val selectableItems: List<SelectableItem<T>>
    val onItemClick: (T) -> Unit
    val hasActiveFilters: Boolean
    val isDropDownVisible: Boolean
    val isSelected: Boolean

    data class MoodFilterChip(
        val content: MoodChipItemContent = MoodChipItemContent(), // Mood-specific content (icons + title)
        override val selectableItems: List<SelectableItem<MoodUi>> = emptyList(),
        override val onItemClick: (MoodUi) -> Unit = {},
        override val hasActiveFilters: Boolean = false,
        override val isDropDownVisible: Boolean = false,
        override val isSelected: Boolean = false,
    ) : FilterChip<MoodUi>

    data class TopicFilterChip(
        val content: TopicChipItemContent = TopicChipItemContent(), // Topic-specific content (title)
        override val selectableItems: List<SelectableItem<String>> = emptyList<String>().asUnselectedItems(),
        override val onItemClick: (String) -> Unit = {},
        override val hasActiveFilters: Boolean = false,
        override val isDropDownVisible: Boolean = false,
        override val isSelected: Boolean = false,
    ) : FilterChip<String>
}
