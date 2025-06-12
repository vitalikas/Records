package lt.vitalijus.records.core.database.record

import androidx.room.Entity
import androidx.room.PrimaryKey
import lt.vitalijus.records.record.presentation.models.MoodUi

@Entity(tableName = RecordEntity.TABLE_NAME)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Int = 0,
    val title: String,
    val moodUi: MoodUi,
    val recordedAt: Long,
    val note: String?,
    val audioFilePath: String,
    val audioPlaybackLength: Long,
    val audioAmplitudes: List<Float>
) {

    companion object {
        const val TABLE_NAME = "records"
        const val COLUMN_ID = "recordId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_FILE_PATH = "filePath"
    }
}
