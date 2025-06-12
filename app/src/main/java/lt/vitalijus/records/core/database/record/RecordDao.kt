package lt.vitalijus.records.core.database.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import lt.vitalijus.records.core.database.record_topic_relation.RecordTopicCrossRef
import lt.vitalijus.records.core.database.record_topic_relation.RecordWithTopics
import lt.vitalijus.records.core.database.topic.TopicEntity

@Dao
interface RecordDao {

    @Query("SELECT * FROM records ORDER BY recordedAt DESC")
    fun observeRecords(): Flow<List<RecordWithTopics>>

    @Query("SELECT * FROM topics ORDER BY topic ASC")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Query(
        """
        SELECT * FROM topics
        WHERE topic LIKE '%' || :query || '%'
        ORDER BY topic ASC
        """
    )
    fun searchTopics(query: String): Flow<List<TopicEntity>>

    @Insert
    suspend fun insertRecord(recordEntity: RecordEntity): Long

    @Upsert
    suspend fun upsertTopic(topicEntity: TopicEntity)

    @Insert
    suspend fun insertRecordTopicCrossRef(recordTopicCrossRef: RecordTopicCrossRef)

    @Transaction
    suspend fun insertRecordWithTopics(recordWithTopics: RecordWithTopics) {
        val recordId = insertRecord(recordWithTopics.recordEntity)

        recordWithTopics.topics.forEach { topic ->
            upsertTopic(topicEntity = topic)
            insertRecordTopicCrossRef(
                recordTopicCrossRef = RecordTopicCrossRef(
                    recordId = recordId.toInt(),
                    topic = topic.topic
                )
            )
        }
    }
}
