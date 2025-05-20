package lt.vitalijus.records.record.presentation.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.record.models.MoodChipContent
import lt.vitalijus.records.record.presentation.record.models.RecordFilterChipType

class RecordViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val _state = MutableStateFlow(RecordState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecordState()
        )

    fun onAction(action: RecordAction) {
        when (action) {
            RecordAction.OnFabClick -> {

            }

            RecordAction.OnFabLongClick -> {

            }

            RecordAction.OnMoodChipClick -> {
                _state.update {
                    it.copy(selectedFilterChipType = RecordFilterChipType.MOOD)
                }
            }

            RecordAction.OnTopicChipClick -> {
                _state.update {
                    it.copy(selectedFilterChipType = RecordFilterChipType.TOPIC)
                }
            }

            is RecordAction.OnRemoveFilters -> {
                when (action.filterType) {
                    RecordFilterChipType.MOOD -> {
                        selectedMoodFilters.update { emptyList() }
                    }

                    RecordFilterChipType.TOPIC -> {
                        selectedTopicFilters.update { emptyList() }
                    }
                }
            }

            RecordAction.OnSettingsClick -> {

            }

            RecordAction.OnDismissTopicDropDown,
            RecordAction.OnDismissMoodDropDown -> {
                _state.update {
                    it.copy(selectedFilterChipType = null)
                }
            }

            is RecordAction.OnFilterByMoodClick -> {
                toggleMoodFilter(action.moodUi)
            }

            is RecordAction.OnFilterByTopicClick -> {
                toggleTopicFilter(action.topic)
            }
        }
    }

    private fun toggleMoodFilter(moodUi: MoodUi) {
        selectedMoodFilters.update { selectedMood ->
            if (moodUi in selectedMood) {
                selectedMood - moodUi
            } else {
                selectedMood + moodUi
            }
        }
    }

    private fun toggleTopicFilter(topic: String) {
        selectedTopicFilters.update { selectedTopic ->
            if (topic in selectedTopic) {
                selectedTopic - topic
            } else {
                selectedTopic + topic
            }
        }
    }

    private fun observeFilters() {
        combine(
            selectedTopicFilters,
            selectedMoodFilters
        ) { selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    topics = it.topics.map { topic ->
                        SelectableItem(
                            item = topic.item,
                            selected = selectedTopics.contains(topic.item)
                        )
                    },
                    moods = MoodUi.entries.map { mood ->
                        SelectableItem(
                            item = mood,
                            selected = selectedMoods.contains(mood)
                        )
                    },
                    hasActiveTopicFilters = selectedTopics.isNotEmpty(),
                    hasActiveMoodFilters = selectedMoods.isNotEmpty(),
                    topicChipTitle = selectedTopics.deriveTopicsToText(),
                    moodChipContent = selectedMoods.asMoodChipContent()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun List<String>.deriveTopicsToText(): UiText {
        return when (size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()}, ${this.last()}")
            else -> {
                val extraElementCount = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElementCount")
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {
        if (this.isEmpty()) {
            return MoodChipContent()
        }

        val moodIcons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }
        return when (size) {
            1 -> MoodChipContent(
                iconsRes = moodIcons,
                title = moodNames.first()
            )

            2 -> MoodChipContent(
                iconsRes = moodIcons,
                title = UiText.Combined(
                    format = "%s, %s",
                    uiTexts = moodNames
                )
            )

            else -> {
                val extraElementCount = size - 2
                MoodChipContent(
                    iconsRes = moodIcons,
                    title = UiText.Combined(
                        format = "%s, %s +$extraElementCount",
                        uiTexts = moodNames.take(2)
                    )
                )
            }
        }
    }
}
