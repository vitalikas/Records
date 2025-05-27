package lt.vitalijus.records.record.presentation.record.models

import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.models.RecordUi

data class RecordDaySection(
    val dateHeader: UiText,
    val records: List<RecordUi>
)
