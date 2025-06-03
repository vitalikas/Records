package lt.vitalijus.records.record.presentation.records.models

import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.util.UiText

data class TopicChipItemContent(
    val text: UiText = UiText.StringResource(R.string.all_topics)
)
