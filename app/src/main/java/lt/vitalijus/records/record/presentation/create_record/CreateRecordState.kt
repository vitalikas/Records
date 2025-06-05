package lt.vitalijus.records.record.presentation.create_record

import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import kotlin.time.Duration

data class CreateRecordState(
    val titleText: String = "",
    val addTopicText: String = "",
    val topics: List<String> = listOf(),
    val noteText: String = "",
    val showMoodSelector: Boolean = true,
    val selectedMoodUi: MoodUi = MoodUi.NEUTRAL,
    val showTopicSuggestions: Boolean = false,
    val moodUi: MoodUi? = null,
    val searchResult: List<SelectableItem<String>> = emptyList(),
    val showCreateTopicOption: Boolean = true,
    val canSaveRecord: Boolean = false,
    val playbackAmplitudes: List<Float> = List(32) { 0.3f },
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    val durationPlayed: Duration = Duration.ZERO,
    val showConfirmLeaveDialog: Boolean = false
) {
    val durationPlayedRatio = (durationPlayed / playbackTotalDuration).toFloat()
}
