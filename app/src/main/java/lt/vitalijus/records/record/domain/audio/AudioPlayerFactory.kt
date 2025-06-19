package lt.vitalijus.records.record.domain.audio

import kotlinx.coroutines.CoroutineScope

interface AudioPlayerFactory {

    fun create(scope: CoroutineScope): AudioPlayer
}
