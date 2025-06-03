package lt.vitalijus.records.record.presentation.records.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lt.vitalijus.records.R
import lt.vitalijus.records.core.presentation.designsystem.theme.Microphone
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.record.presentation.records.models.BubbleFloatingActionButtonColors
import lt.vitalijus.records.record.presentation.records.models.rememberBubbleFloatingActionButtonColors

@Composable
fun RecordBubbleFloatingActionButton(
    showBubble: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    primaryButtonSize: Dp = 56.dp,
    colors: BubbleFloatingActionButtonColors = rememberBubbleFloatingActionButtonColors()
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = modifier
            .background(
                brush = if (showBubble) {
                    colors.outerCircle
                } else {
                    SolidColor(Color.Transparent)
                },
                shape = CircleShape
            )
            .padding(10.dp)
            .background(
                brush = if (showBubble) {
                    colors.innerCircle
                } else {
                    SolidColor(Color.Transparent)
                },
                shape = CircleShape
            )
            .padding(16.dp)
            .background(
                brush = if (isPressed) {
                    colors.primaryPressed
                } else {
                    colors.primary
                },
                shape = CircleShape
            )
            .size(primaryButtonSize)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Preview
@Composable
private fun RecordQuickRecordFloatingActionButtonPreview() {
    RecordsTheme {
        val showBubble = true
        RecordBubbleFloatingActionButton(
            showBubble = showBubble,
            onClick = {},
            icon = {
                Icon(
                    imageVector = if (showBubble) {
                        Icons.Default.Check
                    } else {
                        Icons.Filled.Microphone
                    },
                    contentDescription = if (showBubble) {
                        stringResource(R.string.finish_recording)
                    } else {
                        stringResource(R.string.resume_recording)
                    },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        )
    }
}
