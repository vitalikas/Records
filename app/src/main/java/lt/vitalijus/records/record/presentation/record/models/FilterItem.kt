package lt.vitalijus.records.record.presentation.record.models

import lt.vitalijus.records.record.presentation.models.MoodUi

sealed interface FilterItem {

    data class MoodItem(val moodUi: MoodUi) : FilterItem
    data class TopicItem(val topic: String) : FilterItem
}
