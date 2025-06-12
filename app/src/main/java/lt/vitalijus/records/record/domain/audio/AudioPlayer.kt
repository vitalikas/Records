package lt.vitalijus.records.record.domain.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {

    val activeTrack: StateFlow<AudioTrack>
    fun play(filePath: String, onComplete: () -> Unit)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(filePath: String, onComplete: () -> Unit, progress: Float)
    fun setPendingSeek(progress: Float?)
}
