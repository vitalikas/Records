package lt.vitalijus.records.core.database.record

import androidx.room.TypeConverter

object FloatListTypeConverter {

    @TypeConverter
    fun fromFloatList(list: List<Float>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toFloatList(string: String): List<Float> {
        return string.split(",").map { it.toFloat() }
    }
}
