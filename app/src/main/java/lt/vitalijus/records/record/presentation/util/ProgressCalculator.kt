package lt.vitalijus.records.record.presentation.util

object ProgressCalculator {

    fun calculate(playedDuration: Long?, totalDuration: Long?): Float? {
        if (playedDuration == null || totalDuration == null || totalDuration == 0L) {
            return null
        }
        return playedDuration.toFloat() / totalDuration.toFloat()
    }
}
