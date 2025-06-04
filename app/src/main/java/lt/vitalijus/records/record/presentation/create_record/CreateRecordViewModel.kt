@file:OptIn(FlowPreview::class)

package lt.vitalijus.records.record.presentation.create_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem.Companion.asUnselectedItems

class CreateRecordViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CreateRecordState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeAddTopicText()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateRecordState()
        )

    fun onAction(action: CreateRecordAction) {
        when (action) {
            is CreateRecordAction.OnAddTopicTextChange -> onAddTopicTextChange(action.text)
            CreateRecordAction.OnCancelClick -> TODO()
            CreateRecordAction.OnConfirmMood -> onConfirmMood()
            CreateRecordAction.OnDismissMoodSelector -> onDismissMoodSelector()
            CreateRecordAction.OnDismissTopicSuggestions -> onDismissTopicSuggestions()
            is CreateRecordAction.OnMoodClick -> onMoodClick(action)
            CreateRecordAction.OnNavigateBackClick -> TODO()
            is CreateRecordAction.OnNoteTextChange -> TODO()
            CreateRecordAction.OnPauseAudioClick -> TODO()
            CreateRecordAction.OnPlayAudioClick -> TODO()
            is CreateRecordAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateRecordAction.OnSaveClick -> TODO()
            is CreateRecordAction.OnTitleTextChange -> TODO()
            is CreateRecordAction.OnTopicClick -> onTopicClick(action.topic)
            is CreateRecordAction.OnTrackSizeAvailable -> TODO()
            CreateRecordAction.OnSelectMoodClick -> onSelectMoodClick()
        }
    }

    private fun onDismissTopicSuggestions() {
        _state.update {
            it.copy(
                showTopicSuggestions = false
            )
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        _state.update {
            it.copy(
                topics = it.topics - topic
            )
        }
    }

    private fun onTopicClick(topic: String) {
        _state.update {
            it.copy(
                addTopicText = "",
                topics = (it.topics + topic).distinct()
            )
        }
    }

    private fun onAddTopicTextChange(text: String) {
        _state.update {
            it.copy(
                addTopicText = text.filter { txt ->
                    txt.isLetterOrDigit()
                }
            )
        }
    }

    private fun onConfirmMood() {
        _state.update {
            it.copy(
                moodUi = it.selectedMoodUi,
                canSaveRecord = it.titleText.isNotBlank(),
                showMoodSelector = false
            )
        }
    }

    private fun onDismissMoodSelector() {
        _state.update {
            it.copy(
                showMoodSelector = false
            )
        }
    }

    private fun onMoodClick(action: CreateRecordAction.OnMoodClick) {
        _state.update {
            it.copy(
                selectedMoodUi = action.moodUi
            )
        }
    }

    private fun onSelectMoodClick() {
        _state.update {
            it.copy(
                showMoodSelector = true
            )
        }
    }

    private fun observeAddTopicText() {
        state
            .map {
                it.addTopicText
            }
            .debounce(250)
            .distinctUntilChanged()
            .onEach { query ->
                _state.update {
                    it.copy(
                        showTopicSuggestions = query.isNotBlank() && query.trim() !in it.topics,
                        searchResult = listOf(
                            "hello",
                            "helloworld"
                        ).asUnselectedItems()
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
