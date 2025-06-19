package lt.vitalijus.records.record.domain.settings

import kotlinx.coroutines.flow.Flow
import lt.vitalijus.records.record.domain.record.Mood

interface SettingsPreferences {

    suspend fun saveDefaultTopics(topics: List<String>)
    fun observeDefaultTopics(): Flow<List<String>>

    suspend fun saveDefaultMood(mood: Mood)
    fun observeDefaultMood(): Flow<Mood>
}
