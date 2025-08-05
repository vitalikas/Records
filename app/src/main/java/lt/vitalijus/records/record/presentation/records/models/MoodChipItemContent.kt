package lt.vitalijus.records.record.presentation.records.models

import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.util.UiText

data class MoodChipItemContent(
    val iconsRes: List<Int> = emptyList(),
    val titles: List<UiText> = listOf(UiText.StringResource(R.string.all_moods))
)
