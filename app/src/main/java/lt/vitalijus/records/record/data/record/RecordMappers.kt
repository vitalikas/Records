package lt.vitalijus.records.record.data.record

import lt.vitalijus.records.core.database.record.RecordEntity
import lt.vitalijus.records.core.database.record_topic_relation.RecordWithTopics
import lt.vitalijus.records.core.database.topic.TopicEntity
import lt.vitalijus.records.record.domain.record.Record
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds


fun RecordWithTopics.toRecord(): Record {
    return Record(
        mood = recordEntity.mood,
        title = recordEntity.title,
        note = recordEntity.note,
        topics = topics.map { it.topic },
        audioFilePath = recordEntity.audioFilePath,
        audioPlaybackLength = recordEntity.audioPlaybackLength.milliseconds,
        audioAmplitudes = recordEntity.audioAmplitudes,
        recordedAt = Instant.ofEpochMilli(recordEntity.recordedAt),
        id = recordEntity.recordId
    )
}

fun Record.toRecordWithTopics(): RecordWithTopics {
    return RecordWithTopics(
        recordEntity = RecordEntity(
            recordId = id ?: 0,
            title = title,
            mood = mood,
            recordedAt = recordedAt.toEpochMilli(),
            note = note,
            audioFilePath = audioFilePath,
            audioPlaybackLength = audioPlaybackLength.inWholeMilliseconds,
            audioAmplitudes = audioAmplitudes
        ),
        topics = topics.map {
            TopicEntity(topic = it)
        }
    )
}
