package lt.vitalijus.records.record.data.audio

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.records.record.domain.audio.AudioPlayer
import lt.vitalijus.records.record.domain.audio.AudioTrack
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class AndroidAudioPlayer(
    private val applicationScope: CoroutineScope
) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    private val _activeTrack = MutableStateFlow(AudioTrack())
    override val activeTrack
        get() = _activeTrack.asStateFlow()

    private var durationJob: Job? = null

    override fun play(
        filePath: String,
        onComplete: () -> Unit
    ) {
        mediaPlayer = MediaPlayer().apply {
            val fileInputStream = FileInputStream(File(filePath))
            try {
                setDataSource(fileInputStream.fd)

                prepare()
                start()

                _activeTrack.update {
                    AudioTrack(
                        isPlaying = true
                    )
                }

                trackDuration()

                setOnCompletionListener {
                    onComplete()
                    stop()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error playing audio.")
            } finally {
                fileInputStream.close()
            }
        }
    }

    override fun pause() {
        if (!activeTrack.value.isPlaying) {
            return
        }

        _activeTrack.update {
            it.copy(
                isPlaying = false
            )
        }

        durationJob?.cancel()
        mediaPlayer?.pause()
    }

    override fun resume() {
        if (activeTrack.value.isPlaying) {
            return
        }

        _activeTrack.update {
            it.copy(
                isPlaying = true
            )
        }

        mediaPlayer?.start()
        trackDuration()
    }

    override fun stop() {
        _activeTrack.update {
            it.copy(
                isPlaying = false,
                durationPlayed = ZERO
            )
        }
        durationJob?.cancel()
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    private fun trackDuration() {
        durationJob?.cancel()
        durationJob = applicationScope.launch {
            do {
                _activeTrack.update {
                    it.copy(
                        totalDuration = mediaPlayer?.duration?.milliseconds ?: ZERO,
                        durationPlayed = mediaPlayer?.currentPosition?.milliseconds ?: ZERO
                    )
                }
                delay(10L)
            } while (activeTrack.value.isPlaying && mediaPlayer?.isPlaying == true)
        }
    }
}
