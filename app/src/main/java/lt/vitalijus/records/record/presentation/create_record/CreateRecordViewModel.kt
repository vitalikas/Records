@file:OptIn(FlowPreview::class)

package lt.vitalijus.records.record.presentation.create_record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.records.app.navigation.NavigationRoute
import lt.vitalijus.records.core.presentation.designsystem.dropdowns.SelectableItem.Companion.asUnselectedItems
import lt.vitalijus.records.record.domain.audio.AudioPlayer
import lt.vitalijus.records.record.domain.record.Mood
import lt.vitalijus.records.record.domain.record.Record
import lt.vitalijus.records.record.domain.record.RecordDataSource
import lt.vitalijus.records.record.domain.recording.RecordingStorage
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo
import lt.vitalijus.records.record.presentation.util.AmplitudeNormalizer
import lt.vitalijus.records.record.presentation.util.ProgressCalculator
import lt.vitalijus.records.record.presentation.util.toRecordDetails
import java.time.Instant
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class CreateRecordViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val recordingStorage: RecordingStorage,
    private val audioPlayer: AudioPlayer,
    private val recordDataSource: RecordDataSource
) : ViewModel() {

    private var hasLoadedInitialData = false

    // Getting the navigation arguments
    private val route = savedStateHandle.toRoute<NavigationRoute.CreateRecord>()
    private val recordingDetails = route.toRecordDetails()

    private val eventChannel = Channel<CreateRecordEvent>()
    val events = eventChannel.receiveAsFlow()

    private val restoredTopics = savedStateHandle.get<String>("topics")
        ?.split(",")
        ?.filter { it.isNotBlank() }
        ?: emptyList()
    private val restoredDurationPlayed = savedStateHandle.get<Long>("durationPlayed")
    private val restoredPlaybackTotalDuration = savedStateHandle.get<Long>("playbackTotalDuration")
    private val restoredProgress = ProgressCalculator.calculate(
        playedDuration = restoredDurationPlayed,
        totalDuration = restoredPlaybackTotalDuration
    ) ?: 0f
    private val _state = MutableStateFlow(
        CreateRecordState(
            playbackTotalDuration = savedStateHandle.get<Long>("playbackTotalDuration")?.milliseconds
                ?: recordingDetails.duration,
            titleText = savedStateHandle["titleText"] ?: "",
            noteText = savedStateHandle["noteText"] ?: "",
            topics = restoredTopics,
            moodUi = savedStateHandle.get<String>("mood")?.let {
                MoodUi.valueOf(it)
            },
            showMoodSelector = savedStateHandle.get<String>("mood") == null,
            canSaveRecord = savedStateHandle.get<Boolean>("canSaveRecord") ?: false,
            durationPlayed = savedStateHandle.get<Long>("durationPlayed")?.milliseconds ?: ZERO
        )
    )
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                val progress = restoredProgress
                audioPlayer.setPendingSeek(progress = progress)
                observeAddTopicText()
                hasLoadedInitialData = true
            }
        }
        .onEach { state ->
            savedStateHandle["titleText"] = state.titleText
            savedStateHandle["noteText"] = state.noteText
            savedStateHandle["topics"] = state.topics.joinToString(",")
            savedStateHandle["mood"] = state.moodUi?.name
            savedStateHandle["canSaveRecord"] = state.canSaveRecord
            savedStateHandle["durationPlayed"] = state.durationPlayed.inWholeMilliseconds
            savedStateHandle["playbackTotalDuration"] =
                state.playbackTotalDuration.inWholeMilliseconds
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateRecordState()
        )

    private var durationJob: Job? = null

    fun onAction(action: CreateRecordAction) {
        when (action) {
            is CreateRecordAction.OnAddTopicTextChange -> onAddTopicTextChange(action.text)
            CreateRecordAction.OnConfirmMood -> onConfirmMood()
            CreateRecordAction.OnDismissMoodSelector -> onDismissMoodSelector()
            CreateRecordAction.OnDismissTopicSuggestions -> onDismissTopicSuggestions()
            is CreateRecordAction.OnMoodClick -> onMoodClick(action)
            is CreateRecordAction.OnAddNoteTextChange -> onAddNoteTextChange(action.text)
            CreateRecordAction.OnPauseAudioClick -> onPauseAudioClick()
            CreateRecordAction.OnPlayAudioClick -> onPlayAudioClick()
            is CreateRecordAction.OnSeekAudio -> onSeekAudio(action.progress)
            is CreateRecordAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateRecordAction.OnSaveClick -> onSaveClick()
            is CreateRecordAction.OnAddTitleTextChange -> onTitleTextChange(action.text)
            is CreateRecordAction.OnTopicClick -> onTopicClick(action.topic)
            CreateRecordAction.OnSelectMoodClick -> onSelectMoodClick()
            CreateRecordAction.OnDismissConfirmLeaveDialog -> onDismissConfirmLeaveDialog()
            CreateRecordAction.OnCancelClick,
            CreateRecordAction.OnSystemGoBackClick,
            CreateRecordAction.OnNavigateBackClick -> onShowConfirmLeaveDialog()
        }
    }

    fun onEvent(event: CreateRecordEvent) {
        when (event) {
            is CreateRecordEvent.OnTrackSizeAvailable -> onTrackSizeAvailable(event.trackSizeInfo)
            CreateRecordEvent.FailedToSaveFile -> onFailedToSaveFile()
            CreateRecordEvent.SuccessfullySaved -> onSuccessfullySaved()
        }
    }

    private fun onAddNoteTextChange(text: String) {
        _state.update {
            it.copy(
                noteText = text
            )
        }
    }

    private fun onFailedToSaveFile() {
        viewModelScope.launch {
            eventChannel.send(CreateRecordEvent.FailedToSaveFile)
        }
    }

    private fun onSuccessfullySaved() {

    }

    private fun onPlayAudioClick() {
        if (state.value.playbackState == PlaybackState.PAUSED) {
            audioPlayer.resume()
        } else {
            audioPlayer.play(
                filePath = recordingDetails.tempFilePath
                    ?: throw IllegalArgumentException("Temp file path is null."),
                onComplete = {
                    _state.update {
                        it.copy(
                            playbackState = PlaybackState.STOPPED,
                            durationPlayed = ZERO
                        )
                    }
                }
            )

            durationJob = audioPlayer
                .activeTrack
                .onEach { track ->
                    _state.update {
                        it.copy(
                            durationPlayed = track.durationPlayed,
                            playbackState = if (track.isPlaying) {
                                PlaybackState.PLAYING
                            } else {
                                PlaybackState.PAUSED
                            }
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun onPauseAudioClick() {
        audioPlayer.pause()
    }

    private fun onSeekAudio(progress: Float) {
        audioPlayer.setPendingSeek(null)
        audioPlayer.seekTo(
            filePath = recordingDetails.tempFilePath
                ?: throw IllegalArgumentException("Temp file path is null."),
            onComplete = {
                _state.update {
                    it.copy(
                        playbackState = PlaybackState.STOPPED,
                        durationPlayed = ZERO
                    )
                }
            },
            progress = progress
        )

        _state.update {
            val newDurationPlayed =
                (it.playbackTotalDuration.inWholeMilliseconds * progress).toLong().milliseconds
            it.copy(
                durationPlayed = newDurationPlayed
            )
        }
    }

    private fun onTrackSizeAvailable(trackSizeInfo: TrackSizeInfo) {
        viewModelScope.launch(Dispatchers.Default) {
            val finalAmplitudes = AmplitudeNormalizer.normalize(
                sourceAmplitudes = recordingDetails.amplitudes,
                trackWidth = trackSizeInfo.trackWidth,
                barWidth = trackSizeInfo.barWidth,
                spacing = trackSizeInfo.spacing
            )

            _state.update {
                it.copy(
                    playbackAmplitudes = finalAmplitudes
                )
            }
        }
    }

    private fun onTitleTextChange(text: String) {
        _state.update {
            it.copy(
                titleText = text,
                canSaveRecord = text.isNotBlank() && it.moodUi != null
            )
        }
    }

    private fun onSaveClick() {
        if (recordingDetails.tempFilePath == null || !state.value.canSaveRecord) {
            return
        }

        viewModelScope.launch {
            val persistentFilePath = recordingStorage.savePersistently(
                tempFilePath = recordingDetails.tempFilePath
            )
            if (persistentFilePath == null) {
                onFailedToSaveFile()
                return@launch
            }

            val state = state.value
            val record = Record(
                mood = state.moodUi?.let { moodUi ->
                    Mood.valueOf(moodUi.name)
                } ?: throw IllegalStateException("Mood must be set before saving record."),
                title = state.titleText.trim(),
                note = state.noteText.ifBlank { null },
                topics = state.topics,
                audioFilePath = persistentFilePath,
                audioPlaybackLength = state.playbackTotalDuration,
                audioAmplitudes = state.playbackAmplitudes,
                recordedAt = Instant.now()
            )
            recordDataSource.insertRecord(record = record)
            eventChannel.send(CreateRecordEvent.SuccessfullySaved)
        }
    }

    private fun onShowConfirmLeaveDialog() {
        if (state.value.playbackState == PlaybackState.PLAYING) {
            audioPlayer.pause()
            _state.update {
                it.copy(
                    playbackState = PlaybackState.PAUSED
                )
            }
        }

        _state.update {
            it.copy(
                showConfirmLeaveDialog = true
            )
        }
    }

    private fun onDismissConfirmLeaveDialog() {
        _state.update {
            it.copy(
                showConfirmLeaveDialog = false
            )
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
