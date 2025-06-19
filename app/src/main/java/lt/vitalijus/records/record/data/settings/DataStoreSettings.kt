package lt.vitalijus.records.record.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import lt.vitalijus.records.record.domain.record.Mood
import lt.vitalijus.records.record.domain.settings.SettingsPreferences

class DataStoreSettings(
    private val context: Context
) : SettingsPreferences {

    companion object {
        private val Context.settingsDataStore by preferencesDataStore(
            name = "settings_datastore"
        )
    }

    private val topicsKey = stringSetPreferencesKey("topics")
    private val moodKey = stringPreferencesKey("mood")

    override suspend fun saveDefaultTopics(topics: List<String>) {
        context.settingsDataStore.edit { prefs ->
            prefs[topicsKey] = topics.toSet()
        }
    }

    override fun observeDefaultTopics(): Flow<List<String>> {
        return context
            .settingsDataStore
            .data
            .map { prefs ->
                prefs[topicsKey]?.toList() ?: emptyList()
            }
            .distinctUntilChanged()
    }

    override suspend fun saveDefaultMood(mood: Mood) {
        context.settingsDataStore.edit { prefs ->
            prefs[moodKey] = mood.name
        }
    }

    override fun observeDefaultMood(): Flow<Mood> {
        return context
            .settingsDataStore
            .data
            .map { prefs ->
                Mood.valueOf(prefs[moodKey] ?: Mood.NEUTRAL.name)
            }
            .distinctUntilChanged()
    }
}
