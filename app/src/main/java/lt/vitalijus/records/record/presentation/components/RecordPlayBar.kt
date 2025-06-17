package lt.vitalijus.records.record.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.record.presentation.models.MoodUi
import kotlin.random.Random

@Composable
fun RecordPlayBar(
    amplitudeBarWidth: Dp,
    amplitudeBarSpacing: Dp,
    powerRatios: List<Float>,
    trackColor: Color,
    trackFillColor: Color,
    playerProgress: Float,
    onSeekAudio: (progress: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val progress = (offset.x / size.width).coerceIn(0f, 1f)
                        onSeekAudio(progress)
                    }
                )
            }
    ) {
        val amplitudeBarWidthPx = amplitudeBarWidth.toPx()
        val amplitudeBarSpacingPx = amplitudeBarSpacing.toPx()

        val clipPath = Path()

        powerRatios.forEachIndexed { i, ratio ->
            val height = ratio * size.height

            val xLeftStart = i * (amplitudeBarWidthPx + amplitudeBarSpacingPx)
            val yTopStart = center.y - height / 2f
            val offset = Offset(
                x = xLeftStart,
                y = yTopStart
            )

            val rectSize = Size(
                width = amplitudeBarWidthPx,
                height = height
            )

            val roundRect = RoundRect(
                rect = Rect(
                    offset = offset,
                    size = rectSize
                ),
                cornerRadius = CornerRadius(100f)
            )
            clipPath.addRoundRect(roundRect)

            drawRoundRect(
                color = trackColor,
                topLeft = offset,
                size = rectSize,
                cornerRadius = CornerRadius(100f)
            )
        }

        clipPath(clipPath) {
            drawRect(
                color = trackFillColor,
                size = Size(
                    width = size.width * playerProgress,
                    height = size.height
                )
            )
        }
    }
}

@Preview
@Composable
private fun RecordPlayBarPreview() {
    RecordsTheme {
        val ratios = remember {
            (1..30).map {
                Random.nextFloat()
            }
        }
        RecordPlayBar(
            amplitudeBarWidth = 4.dp,
            amplitudeBarSpacing = 3.dp,
            powerRatios = ratios,
            trackColor = MoodUi.SAD.colorSet.desaturated,
            trackFillColor = MoodUi.SAD.colorSet.vivid,
            playerProgress = 0.23f,
            onSeekAudio = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}
