package lt.vitalijus.records.core.database.record_topic_relation

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import lt.vitalijus.records.core.database.record.RecordEntity
import lt.vitalijus.records.core.database.topic.TopicEntity

@Entity(
    tableName = RecordWithTopics.TABLE_NAME,
    primaryKeys = [RecordEntity.COLUMN_ID, TopicEntity.COLUMN_TOPIC_NAME]
)
data class RecordTopicCrossRef(
    val recordId: Int,
    val topic: String
)

data class RecordWithTopics(
    @Embedded val recordEntity: RecordEntity,
    @Relation(
        parentColumn = RecordEntity.COLUMN_ID,
        entityColumn = TopicEntity.COLUMN_TOPIC_NAME,
        associateBy = Junction(RecordTopicCrossRef::class)
    )
    val topics: List<TopicEntity>
) {

    companion object {
        const val TABLE_NAME = "record_topic_cross_ref"
    }
}
