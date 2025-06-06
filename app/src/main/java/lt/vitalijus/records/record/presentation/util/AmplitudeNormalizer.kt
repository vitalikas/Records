package lt.vitalijus.records.record.presentation.util

import kotlin.math.roundToInt

object AmplitudeNormalizer {

    private const val AMPLITUDE_MIN_OUTPUT_THRESHOLD = 0.1f
    private const val MIN_OUTPUT = 0.25f
    private const val MAX_OUTPUT = 1f

    fun normalize(
        sourceAmplitudes: List<Float>,
        trackWidth: Float,
        barWidth: Float,
        spacing: Float
    ): List<Float> {
        require(trackWidth >= 0) {
            "Track width must be positive."
        }
        require(trackWidth >= barWidth + spacing) {
            "Track width must be at least the size of one bar plus spacing."
        }
        if (sourceAmplitudes.isEmpty()) {
            return emptyList()
        }

        val barsCount = (trackWidth / (barWidth + spacing)).roundToInt()
        val resampledAmplitudes = resampleAmplitudes(sourceAmplitudes, barsCount)
        val remappedAmplitudes = remapAmplitudes(resampledAmplitudes)

        return remappedAmplitudes
    }

    private fun resampleAmplitudes(sourceAmplitudes: List<Float>, targetSize: Int): List<Float> {
        return when {
            targetSize == sourceAmplitudes.size -> sourceAmplitudes
            targetSize < sourceAmplitudes.size -> downsample(sourceAmplitudes, targetSize)
            else -> upsample(sourceAmplitudes, targetSize)
        }
    }

    // [0.5, 0.7, 0.3, 0.4, 0.3, 0.8] -> [0.7, 0.4, 0.8] max value, targetSize is 3
    private fun downsample(sourceAmplitudes: List<Float>, targetSize: Int): List<Float> {
        val ratio = sourceAmplitudes.size.toFloat() / targetSize
        return List(targetSize) { index ->
            val start = (index * ratio).toInt()
            val end = ((index + 1) * ratio).toInt().coerceAtMost(sourceAmplitudes.size)

            sourceAmplitudes.subList(start, end).max()
        }
    }

    // [0, 0.2, 0.3] -> [0, x1, 0.2, x2, 0.3], targetSize is 5 -> x1 = 0.1, x2 = 0.25 (avg of left and right)
    // [0, 0.1, 0] -> [0, x1, x2, x3, 0.1, x4, x5, x6, 0], targetSize is 9 -> x1 = 0.025, x2 = 0.05, x3 = 0.075, x4 = 0.075, x5 = 0.05, x6 = 0.025 (interpolation)
    private fun upsample(sourceAmplitudes: List<Float>, targetSize: Int): List<Float> {
        val result = mutableListOf<Float>()

        val step = (sourceAmplitudes.size - 1).toFloat() / (targetSize - 1)
        for (i in 0 until targetSize) {
            // How far we moved along the source list
            val pos = i * step
            // Which existing element lies exactly to the left of this position?
            val index = pos.toInt()

            // How far we are past that item as a percentage of the gap to the next one
            val fraction = pos - index

            val value = if (index + 1 < sourceAmplitudes.size) {
                (1 - fraction) * sourceAmplitudes[index] + fraction * sourceAmplitudes[index + 1]
            } else {
                sourceAmplitudes[index]
            }

            result.add(value)
        }

        return result.toList()
    }

    private fun remapAmplitudes(amplitudes: List<Float>): List<Float> {
        val outputRange = MAX_OUTPUT - MIN_OUTPUT
        val scaleFactor = MAX_OUTPUT - AMPLITUDE_MIN_OUTPUT_THRESHOLD
        return amplitudes.map { amplitude ->
            if (amplitude <= AMPLITUDE_MIN_OUTPUT_THRESHOLD) {
                MIN_OUTPUT
            } else {
                val amplitudeRange = amplitude - AMPLITUDE_MIN_OUTPUT_THRESHOLD

                MIN_OUTPUT + (amplitudeRange / scaleFactor) * outputRange
            }
        }
    }
}
