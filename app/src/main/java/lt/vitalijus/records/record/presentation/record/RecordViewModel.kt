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
import lt.vitalijus.records.record.presentation.record.models.FilterItem
import lt.vitalijus.records.record.presentation.record.models.MoodChipItemContent
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

            is RecordAction.OnFilterChipClick -> {
                val type = action.chipType
                when (type) {
                    RecordFilterChipType.MOOD -> {
                        _state.update {
                            it.copy(
                                moodFilterChipData = it.moodFilterChipData.copy(
                                    isDropDownVisible = !it.moodFilterChipData.isDropDownVisible
                                )
                            )
                        }
                    }

                    RecordFilterChipType.TOPIC -> {
                        _state.update {
                            it.copy(
                                topicFilterChipData = it.topicFilterChipData.copy(
                                    isDropDownVisible = !it.topicFilterChipData.isDropDownVisible
                                )
                            )
                        }
                    }
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

            is RecordAction.OnDismissFilterDropDown -> {
                when (action.filterType) {
                    RecordFilterChipType.MOOD -> {
                        _state.update {
                            it.copy(
                                moodFilterChipData = it.moodFilterChipData.copy(
                                    isDropDownVisible = false
                                )
                            )
                        }
                    }

                    RecordFilterChipType.TOPIC -> {
                        _state.update {
                            it.copy(
                                topicFilterChipData = it.topicFilterChipData.copy(
                                    isDropDownVisible = false
                                )
                            )
                        }
                    }
                }
            }

            is RecordAction.OnFilterByItem -> {
                when (action.filterItem) {
                    is FilterItem.MoodItem -> {
                        toggleMoodFilter(moodUi = action.filterItem.moodUi)
                    }

                    is FilterItem.TopicItem -> {
                        toggleTopicFilter(topic = action.filterItem.topic)
                    }
                }
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
            selectedMoodFilters,
            selectedTopicFilters
        ) { selectedMoods, selectedTopics ->
            _state.update {
                it.copy(
                    moodFilterChipData = it.moodFilterChipData.copy(
                        selectableItems = MoodUi.entries.map { mood ->
                            SelectableItem(
                                item = mood,
                                selected = selectedMoods.contains(mood)
                            )
                        },
                        content = selectedMoods.asMoodChipContent(),
                        hasActiveFilters = selectedMoods.isNotEmpty()
                    ),
                    topicFilterChipData = it.topicFilterChipData.copy(
                        selectableItems = it.topicFilterChipData.selectableItems.map { topic ->
                            SelectableItem(
                                item = topic.item,
                                selected = selectedTopics.contains(topic.item)
                            )
                        },
                        content = it.topicFilterChipData.content.copy(
                            text = selectedTopics.deriveTopicsToText()
                        ),
                        hasActiveFilters = selectedTopics.isNotEmpty()
                    )
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

    private fun List<MoodUi>.asMoodChipContent(): MoodChipItemContent {
        if (this.isEmpty()) {
            return MoodChipItemContent()
        }

        val moodIcons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }
        return when (size) {
            1 -> MoodChipItemContent(
                iconsRes = moodIcons,
                title = moodNames.first()
            )

            2 -> MoodChipItemContent(
                iconsRes = moodIcons,
                title = UiText.Combined(
                    format = "%s, %s",
                    uiTexts = moodNames
                )
            )

            else -> {
                val extraElementCount = size - 2
                MoodChipItemContent(
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
