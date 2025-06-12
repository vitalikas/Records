package lt.vitalijus.records.core.database.topic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TopicEntity.TABLE_NAME)
data class TopicEntity(
    @PrimaryKey(autoGenerate = false)
    val topic: String
) {

    companion object {
        const val TABLE_NAME = "topics"
        const val COLUMN_TOPIC_NAME = "topic"
    }
}
