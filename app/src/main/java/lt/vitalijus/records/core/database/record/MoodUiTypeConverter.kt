package lt.vitalijus.records.core.database.record

import androidx.room.TypeConverter
import lt.vitalijus.records.record.domain.record.Mood

object MoodUiTypeConverter {

    @TypeConverter
    fun fromMood(mood: Mood): String {
        return mood.name
    }

    @TypeConverter
    fun toMood(moodName: String): Mood {
        return Mood.valueOf(moodName)
    }
}
