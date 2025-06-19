package lt.vitalijus.records.record.domain.recording

import kotlinx.coroutines.CoroutineScope

interface VoiceRecorderFactory {

    fun create(scope: CoroutineScope): VoiceRecorder
}
