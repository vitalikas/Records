package lt.vitalijus.records.record.data.record

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lt.vitalijus.records.core.database.record.RecordDao
import lt.vitalijus.records.record.domain.record.Record
import lt.vitalijus.records.record.domain.record.RecordDataSource

class RoomRecordDataSource(
    private val recordDao: RecordDao
) : RecordDataSource {

    override fun observeRecords(): Flow<List<Record>> {
        return recordDao
            .observeRecords()
            .map {
                it.map { recordWithTopics ->
                    recordWithTopics.toRecord()
                }
            }
    }

    override fun observeTopics(): Flow<List<String>> {
        return recordDao
            .observeTopics()
            .map {
                it.map { topicEntity ->
                    topicEntity.topic
                }
            }
    }

    override fun searchTopics(query: String): Flow<List<String>> {
        return recordDao
            .searchTopics(query = query)
            .map {
                it.map { topicEntity ->
                    topicEntity.topic
                }
            }
    }

    override suspend fun insertRecord(record: Record) {
        val recordEntity = record
            .toRecordWithTopics()
            .recordEntity
        recordDao.insertRecordWithTopics(recordWithTopics = record.toRecordWithTopics())
    }
}
