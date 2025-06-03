package lt.vitalijus.records.record.presentation.create_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CreateRecordViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CreateRecordState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
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
            is CreateRecordAction.OnAddTopicTextChange -> TODO()
            CreateRecordAction.OnCancelClick -> TODO()
            CreateRecordAction.OnConfirmMood -> onConfirmMood()
            CreateRecordAction.OnCreateNewTopicClick -> TODO()
            CreateRecordAction.OnDismissMoodSelector -> onDismissMoodSelector()
            CreateRecordAction.OnDismissTopicSuggestions -> TODO()
            is CreateRecordAction.OnMoodClick -> onMoodClick(action)
            CreateRecordAction.OnNavigateBackClick -> TODO()
            is CreateRecordAction.OnNoteTextChange -> TODO()
            CreateRecordAction.OnPauseAudioClick -> TODO()
            CreateRecordAction.OnPlayAudioClick -> TODO()
            is CreateRecordAction.OnRemoveTopicClick -> TODO()
            CreateRecordAction.OnSaveClick -> TODO()
            is CreateRecordAction.OnTitleTextChange -> TODO()
            is CreateRecordAction.OnTopicClick -> TODO()
            is CreateRecordAction.OnTrackSizeAvailable -> TODO()
            CreateRecordAction.OnSelectMoodCick -> onSelectMoodClick()
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
}
