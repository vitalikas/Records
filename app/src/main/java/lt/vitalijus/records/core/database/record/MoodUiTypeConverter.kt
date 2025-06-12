package lt.vitalijus.records.core.database.record

import androidx.room.TypeConverter
import lt.vitalijus.records.record.presentation.models.MoodUi

object MoodUiTypeConverter {

    @TypeConverter
    fun fromMoodUi(moodUi: MoodUi): String {
        return moodUi.name
    }

    @TypeConverter
    fun toMoodUi(moodUiString: String): MoodUi {
        return MoodUi.valueOf(moodUiString)
    }
}
