package lt.vitalijus.records.record.presentation.records

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
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
import lt.vitalijus.records.record.domain.audio.AudioPlayerFactory
import lt.vitalijus.records.record.domain.record.Record
import lt.vitalijus.records.record.domain.record.RecordDataSource
import lt.vitalijus.records.record.domain.recording.VoiceRecorderFactory
import lt.vitalijus.records.record.presentation.models.MoodUi
import lt.vitalijus.records.record.presentation.models.RecordUi
import lt.vitalijus.records.record.presentation.records.models.AudioCaptureMethod
import lt.vitalijus.records.record.presentation.records.models.FilterItem
import lt.vitalijus.records.record.presentation.records.models.MoodChipItemContent
import lt.vitalijus.records.record.presentation.records.models.PlaybackState
import lt.vitalijus.records.record.presentation.records.models.RecordFilterChipType
import lt.vitalijus.records.record.presentation.records.models.RecordingType
import lt.vitalijus.records.record.presentation.records.models.TrackSizeInfo
import lt.vitalijus.records.record.presentation.util.AmplitudeNormalizer
import lt.vitalijus.records.record.presentation.util.ProgressCalculator
import lt.vitalijus.records.record.presentation.util.toRecordUi
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RecordsViewModel(
    private val savedStateHandle: SavedStateHandle,
    voiceRecorderFactory: VoiceRecorderFactory,
    audioPlayerFactory: AudioPlayerFactory,
    private val recordDataSource: RecordDataSource
) : ViewModel() {

    companion object {
        private val MIN_RECORD_DURATION = 1.5.seconds
    }

    private val voiceRecorder = voiceRecorderFactory.create(scope = viewModelScope)
    private val audioPlayer = audioPlayerFactory.create(scope = viewModelScope)

    private var hasLoadedInitialData = false

    private val playingRecordId = MutableStateFlow<Int?>(null)

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())

    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val audioTrackSizeInfo = MutableStateFlow<TrackSizeInfo?>(null)

    private val eventChannel = Channel<RecordsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val restoredSelectedRecordId = savedStateHandle.get<Int>("selectedRecordId")
    private val restoredProgress = savedStateHandle.get<Float>("progress")

    private val _state = MutableStateFlow(RecordsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                observeRecords()
                fetchNavigationArgs()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecordsState()
        )

    private val filteredRecords = recordDataSource
        .observeRecords()
        .filterByMoodAndTopics()
        .onEach { records ->
            _state.update {
                it.copy(
                    hasRecorded = records.isNotEmpty(),
                    isLoadingData = false,
                )
            }
        }
        .combine(audioTrackSizeInfo) { records, trackSizeInfo ->
            trackSizeInfo?.let { trackSize ->
                records.map { record ->
                    record.copy(
                        audioAmplitudes = AmplitudeNormalizer.normalize(
                            sourceAmplitudes = record.audioAmplitudes,
                            trackWidth = trackSize.trackWidth,
                            barWidth = trackSize.barWidth,
                            spacing = trackSize.spacing
                        )
                    )
                }
            } ?: records
        }
        .flowOn(Dispatchers.Default)

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
                audioTrackSizeInfo.update {
                    action.trackSizeInfo
                }
            }

            RecordsAction.OnCancelRecording -> cancelRecording()

            RecordsAction.OnCompleteRecording -> stopRecording()

            RecordsAction.OnPauseRecording -> pauseRecording()

            RecordsAction.OnResumeRecording -> resumeRecording()

            RecordsAction.OnRecordsButtonLongClick -> startRecording(captureMethod = AudioCaptureMethod.QUICK)

            is RecordsAction.OnSeekAudio -> onSeekAudio(
                recordId = action.recordId,
                progress = action.progress
            )
        }
    }

    private fun fetchNavigationArgs() {
        val startRecording = savedStateHandle["startRecording"] ?: false
        if (startRecording) {
            viewModelScope.launch {
                eventChannel.send(
                    RecordsEvent.AudioPermission.OnRequest(captureMethod = AudioCaptureMethod.STANDARD)
                )
            }

            _state.update {
                it.copy(
                    currentCaptureMethod = AudioCaptureMethod.STANDARD
                )
            }
        }
    }

    private fun observeRecords() {
        combine(
            filteredRecords,
            playingRecordId,
            audioPlayer.activeTrack
        ) { records, playingRecordId, activeTrack ->
            if (playingRecordId == null) {
                return@combine records.map { record ->
                    record.toRecordUi()
                }
            }

            records.map { record ->
                if (record.id == playingRecordId) {
                    record.toRecordUi(
                        currentPlaybackDuration = activeTrack.durationPlayed,
                        playbackState = if (activeTrack.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
                    )
                } else {
                    record.toRecordUi()
                }
            }
        }
            .groupByRelativeDate()
            .onStart {
                val recordList = filteredRecords.firstOrNull()
                val recordToSeek = recordList?.firstOrNull { it.id == restoredSelectedRecordId }
                if (recordToSeek != null) {
                    recordList.forEach { record ->
                        playingRecordId.update { restoredSelectedRecordId }
                        audioPlayer.seekTo(
                            filePath = record.audioFilePath,
                            onComplete = ::completePlayback,
                            progress = restoredProgress ?: 0f
                        )
                        record.toRecordUi(
                            currentPlaybackDuration = restoredProgress?.toDouble()?.milliseconds
                                ?: ZERO,
                            playbackState = if (audioPlayer.activeTrack.value.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
                        )
                    }
                }
            }
            .onEach { groupedRecords ->
                _state.update {
                    it.copy(
                        records = groupedRecords
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
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

        savedStateHandle["selectedRecordId"] = recordId
    }

    private fun onPauseAudioClick() {
        audioPlayer.pause()

        val activeTrackValue = audioPlayer.activeTrack.value
        val durationPlayedMs = activeTrackValue.durationPlayed.inWholeMilliseconds
        val totalDurationMs = activeTrackValue.totalDuration.inWholeMilliseconds

        val progress = ProgressCalculator.calculate(
            playedDuration = durationPlayedMs,
            totalDuration = totalDurationMs
        )

        savedStateHandle["progress"] = progress?.coerceIn(0f, 1f) ?: 0f
    }

    private fun onSeekAudio(
        recordId: Int,
        progress: Float
    ) {
        val selectedRecord = state.value.records.values
            .flatten()
            .firstOrNull { it.id == recordId }

        if (selectedRecord == null) {
            return
        }

        val currentlyPlayingId = playingRecordId.value
        if (currentlyPlayingId != recordId) {
            playingRecordId.update { recordId }
        }

        audioPlayer.setPendingSeek(null)
        audioPlayer.seekTo(
            filePath = selectedRecord.audioFilePath,
            onComplete = ::completePlayback,
            progress = progress
        )

        savedStateHandle["selectedRecordId"] = recordId
        savedStateHandle["progress"] = progress
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
                        RecordsEvent.RecordsState.OnDone(
                            recordingDetails = recordingDetails.copy(
                                // Arbitrary track dimensions to not make the app crash
                                // when navigating and passing the amplitudes as an argument.
                                amplitudes = AmplitudeNormalizer.normalize(
                                    sourceAmplitudes = recordingDetails.amplitudes,
                                    trackWidth = 10_000f,
                                    barWidth = 20f,
                                    spacing = 15f
                                )
                            )
                        )
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
                recordingType = RecordingType.NOT_RECORDING,
                currentCaptureMethod = null
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
            recordDataSource.observeTopics(),
            selectedMoodFilters,
            selectedTopicFilters
        ) { allTopics, selectedMoods, selectedTopics ->
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
                        selectableItems = allTopics.map { topic ->
                            SelectableItem(
                                item = topic,
                                selected = selectedTopics.contains(topic)
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

    private fun Flow<List<Record>>.filterByMoodAndTopics(): Flow<List<Record>> {
        return combine(
            this@filterByMoodAndTopics,
            selectedMoodFilters,
            selectedTopicFilters
        ) { records, moods, topics ->
            records.filter { record ->
                val matchesMood = moods
                    .takeIf { moods ->
                        moods.isNotEmpty()
                    }
                    ?.any { mood ->
                        mood.name == record.mood.name
                    }
                    ?: true

                val matchesTopics = topics
                    .takeIf { topics ->
                        topics.isNotEmpty()
                    }
                    ?.any { topic ->
                        topic in record.topics
                    }
                    ?: true

                matchesMood && matchesTopics
            }
        }
    }

    private fun Flow<List<RecordUi>>.groupByRelativeDate(): Flow<Map<UiText, List<RecordUi>>> {
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val today = LocalDate.now()

        return map { records ->
            records
                .groupBy { record ->
                    LocalDate.ofInstant(
                        record.recordedAt,
                        ZoneId.systemDefault()
                    )
                }
                .mapValues { (_: LocalDate, records) ->
                    records.sortedByDescending { it.recordedAt }
                }
                .toSortedMap(compareByDescending { it })
                .mapKeys { (date, _) ->
                    when (date) {
                        today -> UiText.StringResource(R.string.today)
                        today.minusDays(1) -> UiText.StringResource(R.string.yesterday)
                        else -> UiText.Dynamic(date.format(formatter))
                    }
                }
        }
    }
}
