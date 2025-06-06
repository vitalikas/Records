package lt.vitalijus.records.record.data.recording

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import lt.vitalijus.records.record.domain.recording.RecordingStorage
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.temporal.ChronoUnit

class InternalRecordingStorage(
    private val context: Context
) : RecordingStorage {

    override suspend fun savePersistently(tempFilePath: String): String? {
        val tempFile = File(tempFilePath)
        if (!tempFile.exists()) {
            Timber.e("The temporary file does not exist.")
            return null
        }

        return withContext(Dispatchers.IO) {
            try {
                val persistentFile = generatePersistentFile()
                tempFile.copyTo(persistentFile)
                persistentFile.absolutePath
            } catch (e: IOException) {
                Timber.e(e, "Failed to save recording to persistent storage.")
                null
            } finally {
                withContext(NonCancellable) {
                    cleanUpTemporaryFiles()
                }
            }
        }
    }

    override suspend fun cleanUpTemporaryFiles() {
        withContext(Dispatchers.IO) {
            context
                .cacheDir
                .listFiles()
                ?.filter { it.name.startsWith(RecordingStorage.TEMP_FILE_PREFIX) }
                ?.forEach { file ->
                    file.delete()
                }
        }
    }

    private fun generatePersistentFile(): File {
        val timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        return File(
            context.filesDir,
            "${RecordingStorage.PERSISTENT_FILE_PREFIX}_$timestamp.${RecordingStorage.RECORDING_FILE_EXTENSION}"
        )
    }
}
