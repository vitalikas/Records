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
import lt.vitalijus.records.record.domain.audio.AudioPlayer
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.records.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.records.models.FilterItem
import lt.vitalijus.records.record.presentation.records.models.MoodChipItemContent
import lt.vitalijus.records.record.presentation.records.models.RecordFilterChipType
import lt.vitalijus.records.record.presentation.records.models.RecordingType
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

class RecordsViewModel(
    private val voiceRecorder: AndroidVoiceRecorder,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    companion object {
        private val MIN_RECORD_DURATION = 1.5.seconds
    }

    private var hasLoadedInitialData = false

    private val playingRecordId = MutableStateFlow<Int?>(null)

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())

    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val eventChannel = Channel<RecordsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(RecordsState())
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
            initialValue = RecordsState()
        )

    fun onAction(action: RecordsAction) {
        when (action) {
            is RecordsAction.OnFilterChipClick -> {
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

            is RecordsAction.OnRemoveFilters -> {
                when (action.filterType) {
                    RecordFilterChipType.MOOD -> {
                        selectedMoodFilters.update { emptyList() }
                    }

                    RecordFilterChipType.TOPIC -> {
                        selectedTopicFilters.update { emptyList() }
                    }
                }
            }

            RecordsAction.OnSettingsClick -> {

            }

            is RecordsAction.OnDismissFilterDropDown -> {
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

            is RecordsAction.OnFilterByItem -> {
                when (action.filterItem) {
                    is FilterItem.MoodItem -> {
                        toggleMoodFilter(moodUi = action.filterItem.moodUi)
                    }

                    is FilterItem.TopicItem -> {
                        toggleTopicFilter(topic = action.filterItem.topic)
                    }
                }
            }

            is RecordsAction.OnPlayAudioClick -> onPlayAudioClick(action.recordId)

            RecordsAction.OnPauseAudioClick -> onPauseAudioClick()

            is RecordsAction.OnTrackSizeAvailable -> {

            }

            RecordsAction.OnCancelRecording -> cancelRecording()

            RecordsAction.OnCompleteRecording -> stopRecording()

            RecordsAction.OnPauseRecording -> pauseRecording()

            RecordsAction.OnResumeRecording -> resumeRecording()

            RecordsAction.OnRecordsButtonLongClick -> startRecording(captureMethod = AudioCaptureMethod.QUICK)

            is RecordsAction.OnSeekAudio -> {}
        }
    }

    private fun onPauseAudioClick() {
        audioPlayer.pause()
    }

    private fun onPlayAudioClick(recordId: Int) {
        val selectedRecord = state.value.records.values.flatten().first { it.id == recordId }
        val activeTrack = audioPlayer.activeTrack.value
        val isNewRecord = playingRecordId.value != recordId
        val isSameRecordFromBeginning =
            playingRecordId.value == recordId && activeTrack.durationPlayed == ZERO

        when {
            isNewRecord || isSameRecordFromBeginning -> {
                playingRecordId.update { recordId }
                audioPlayer.stop()
                audioPlayer.play(
                    filePath = selectedRecord.audioFilePath,
                    onComplete = ::completePlayback
                )
            }

            else -> audioPlayer.resume()
        }
    }

    private fun completePlayback() {
        _state.update {
            it.copy(
                records = it.records.mapValues { (_, records) ->
                    records.map { record ->
                        record.copy(
                            playbackCurrentDuration = ZERO
                        )
                    }
                }
            )
        }

        playingRecordId.update { null }
    }

    fun onEvent(event: RecordsEvent) {
        when (event) {
            RecordsEvent.AudioPermission.OnGranted -> {
                startRecording(
                    captureMethod = AudioCaptureMethod.STANDARD
                )
            }

            is RecordsEvent.AudioPermission.OnRequest -> {
                val captureMethod = event.captureMethod
                viewModelScope.launch {
                    eventChannel.send(
                        RecordsEvent.AudioPermission.OnRequest(captureMethod = captureMethod)
                    )
                }
                _state.update {
                    it.copy(
                        currentCaptureMethod = captureMethod
                    )
                }
            }

            is RecordsEvent.RecordsState.OnDone -> {
                val recordingDetails = voiceRecorder.recordingDetails.value
                viewModelScope.launch {
                    eventChannel.send(
                        RecordsEvent.RecordsState.OnDone(recordingDetails = recordingDetails)
                    )
                }
            }

            RecordsEvent.RecordsState.OnTooShort -> {
                viewModelScope.launch {
                    eventChannel.send(
                        RecordsEvent.RecordsState.OnTooShort
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
                recordingType = RecordingType.RECORDING,
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
                recordingType = RecordingType.PAUSED
            )
        }
    }

    private fun resumeRecording() {
        voiceRecorder.resume()
        _state.update {
            it.copy(
                recordingType = RecordingType.RECORDING
            )
        }
    }

    private fun cancelRecording() {
        voiceRecorder.cancel()
        _state.update {
            it.copy(
                recordingType = RecordingType.NOT_RECORDING,
                currentCaptureMethod = null
            )
        }
    }

    private fun stopRecording() {
        voiceRecorder.stop()
        _state.update {
            it.copy(
                recordingType = RecordingType.NOT_RECORDING
            )
        }
        val recordingDetails = voiceRecorder.recordingDetails.value
        viewModelScope.launch {
            if (recordingDetails.duration < MIN_RECORD_DURATION) {
                eventChannel.send(RecordsEvent.RecordsState.OnTooShort)
            } else {
                eventChannel.send(RecordsEvent.RecordsState.OnDone(recordingDetails = recordingDetails))
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
