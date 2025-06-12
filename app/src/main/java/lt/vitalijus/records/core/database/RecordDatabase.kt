package lt.vitalijus.records.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import lt.vitalijus.records.core.database.record.FloatListTypeConverter
import lt.vitalijus.records.core.database.record.MoodUiTypeConverter
import lt.vitalijus.records.core.database.record.RecordDao
import lt.vitalijus.records.core.database.record.RecordEntity
import lt.vitalijus.records.core.database.record_topic_relation.RecordTopicCrossRef
import lt.vitalijus.records.core.database.topic.TopicEntity

@Database(
    entities = [RecordEntity::class, TopicEntity::class, RecordTopicCrossRef::class],
    version = 1,
)
@TypeConverters(
    FloatListTypeConverter::class,
    MoodUiTypeConverter::class
)
abstract class RecordDatabase : RoomDatabase() {

    abstract val recordDao: RecordDao
}
