package lt.vitalijus.records.record.presentation.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem
import lt.vitalijus.records.core.presentation.util.UiText
import lt.vitalijus.records.record.data.recording.AndroidVoiceRecorder
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.records.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.records.models.FilterItem
import lt.vitalijus.records.record.presentation.records.models.MoodChipItemContent
import lt.vitalijus.records.record.presentation.records.models.RecordFilterChipType
import lt.vitalijus.records.record.presentation.records.models.RecordingState
import kotlin.time.Duration.Companion.seconds

class RecordViewModel(
    private val voiceRecorder: AndroidVoiceRecorder
) : ViewModel() {

    companion object {
        private val MIN_RECORD_DURATION = 1.5.seconds
    }

    private var hasLoadedInitialData = false

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val eventChannel = Channel<RecordEvent>()
    val events = eventChannel.receiveAsFlow()

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

            is RecordAction.OnPlayAudioClick -> {

            }

            RecordAction.OnPauseAudioClick -> {

            }

            is RecordAction.OnTrackSizeAvailable -> {

            }

            RecordAction.OnCancelRecording -> {
                cancelRecording()
            }

            RecordAction.OnCompleteRecording -> {
                stopRecording()
            }

            RecordAction.OnPauseRecording -> {
                pauseRecording()
            }

            RecordAction.OnResumeRecording -> {
                resumeRecording()
            }

            RecordAction.OnRecordButtonLongClick -> {
                startRecording(
                    captureMethod = AudioCaptureMethod.QUICK
                )
            }
        }
    }

    fun onEvent(event: RecordEvent) {
        when (event) {
            RecordEvent.AudioPermission.OnGranted -> {
                startRecording(
                    captureMethod = AudioCaptureMethod.STANDARD
                )
            }

            is RecordEvent.AudioPermission.OnRequest -> {
                val captureMethod = event.captureMethod
                viewModelScope.launch {
                    eventChannel.send(
                        RecordEvent.AudioPermission.OnRequest(captureMethod = captureMethod)
                    )
                }
                _state.update {
                    it.copy(
                        currentCaptureMethod = captureMethod
                    )
                }
            }

            is RecordEvent.RecordState.OnDone -> {
                val recordingDetails = voiceRecorder.recordingDetails.value
                viewModelScope.launch {
                    eventChannel.send(
                        RecordEvent.RecordState.OnDone(recordingDetails = recordingDetails)
                    )
                }
            }

            RecordEvent.RecordState.OnTooShort -> {
                viewModelScope.launch {
                    eventChannel.send(
                        RecordEvent.RecordState.OnTooShort
                    )
                }
            }
        }
    }

    private fun startRecording(
        captureMethod: AudioCaptureMethod
    ) {
        _state.update {
            it.copy(
                recordingState = RecordingState.RECORDING,
                currentCaptureMethod = captureMethod
            )
        }

        voiceRecorder.start()

        if (captureMethod == AudioCaptureMethod.STANDARD) {
            voiceRecorder.recordingDetails
                .distinctUntilChangedBy { it.duration }
                .map { recordingDetails ->
                    recordingDetails.duration
                }
                .onEach { duration ->
                    _state.update {
                        it.copy(
                            recordingElapsedDuration = duration
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun pauseRecording() {
        voiceRecorder.pause()
        _state.update {
            it.copy(
                recordingState = RecordingState.PAUSED
            )
        }
    }

    private fun resumeRecording() {
        voiceRecorder.resume()
        _state.update {
            it.copy(
                recordingState = RecordingState.RECORDING
            )
        }
    }

    private fun cancelRecording() {
        voiceRecorder.cancel()
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING,
                currentCaptureMethod = null
            )
        }
    }

    private fun stopRecording() {
        voiceRecorder.stop()
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING
            )
        }
        val recordingDetails = voiceRecorder.recordingDetails.value
        viewModelScope.launch {
            if (recordingDetails.duration < MIN_RECORD_DURATION) {
                eventChannel.send(RecordEvent.RecordState.OnTooShort)
            } else {
                eventChannel.send(RecordEvent.RecordState.OnDone(recordingDetails = recordingDetails))
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
