package lt.vitalijus.records.record.domain.recording

interface RecordingStorage {

    suspend fun savePersistently(tempFilePath: String): String?
    suspend fun cleanUpTemporaryFiles()

    companion object {
        const val TEMP_FILE_PREFIX = "temp_recording"
        const val PERSISTENT_FILE_PREFIX = "recording"
        const val TEMP_FILE_SUFFIX = ".mp4"
        const val RECORDING_FILE_EXTENSION = "mp4"
    }
}
